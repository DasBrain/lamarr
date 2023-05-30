package pw.dasbrain.lamarr.agent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.net.InetAddress;
import java.net.Socket;

import jdk.jshell.execution.RemoteExecutionControl;
import jdk.jshell.execution.Util;

class InstrumentationRemoteExecutionControl extends RemoteExecutionControl implements Runnable {
    
    private final Instrumentation inst;
    private final String arg;
    
    public InstrumentationRemoteExecutionControl(Instrumentation inst, String arg) {
        this.inst = inst;
        this.arg = arg;
    }
    
    @Override
    public void redefine(ClassBytecodes[] cbcs)
            throws ClassInstallException, NotImplementedException, EngineTerminationException {
        try {
            ClassDefinition[] defs = new ClassDefinition[cbcs.length];
            for (int i = 0; i < cbcs.length; i++) {
                defs[i] = new ClassDefinition(findClass(cbcs[i].name()), cbcs[i].bytecodes());
            }
            inst.redefineClasses(defs);
        } catch (ClassNotFoundException | UnmodifiableClassException
                | UnsupportedOperationException e) {
            // In this case no classes have been redefined.
            ClassInstallException cie = new ClassInstallException(e.getMessage(), new boolean[cbcs.length]);
            cie.initCause(e);
            throw cie;
        }
        super.redefine(cbcs);
    }
    
    @Override
    public void run() {
        try (Socket s = new Socket(InetAddress.getLoopbackAddress(), Integer.parseInt(arg));
                ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(s.getInputStream());) {
            out.flush();
            Util.forwardExecutionControl(this, in, out);
        } catch (IOException  e) {
            // TODO: Better error handling
            throw new InternalError(e);
        }
    }
}
