package pw.dasbrain.lamarr;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import jdk.jshell.execution.StreamingExecutionControl;
import jdk.jshell.spi.ExecutionControl;
import jdk.jshell.spi.ExecutionControlProvider;
import jdk.jshell.spi.ExecutionEnv;

public class InstrumentationExecutionControlProvider implements ExecutionControlProvider {
    
    @Override
    public String name() {
        return "instrumentation";
    }
    
    @Override
    public ExecutionControl generate(ExecutionEnv env, Map<String, String> parameters)
            throws Throwable {
        ServerSocket so = new ServerSocket(0);
        so.setSoTimeout(5000);
        attach(parameters.get("pid"), so.getLocalPort());
        Socket s = so.accept();
        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
        out.flush();
        ObjectInputStream in = new ObjectInputStream(s.getInputStream());
        return new StreamingExecutionControl(out, in);
    }
    
    @Override
    public Map<String, String> defaultParameters() {
        Map<String, String> result = new HashMap<>();
        result.put("pid", "");
        return result;
    }
    
    private static void attach(String pid, int port) throws AttachNotSupportedException,
            IOException, AgentLoadException, AgentInitializationException, URISyntaxException {
        VirtualMachine vm = VirtualMachine.attach(pid);
        try {
            vm.loadAgent(new File(InstrumentationExecutionControlProvider.class
                    .getProtectionDomain().getCodeSource().getLocation().toURI()).toString(),
                    port + "");
        } finally {
            vm.detach();
        }
    }
}
