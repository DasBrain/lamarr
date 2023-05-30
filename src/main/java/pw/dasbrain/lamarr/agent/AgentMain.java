package pw.dasbrain.lamarr.agent;

import java.lang.instrument.Instrumentation;

public class AgentMain {
    
    public static void agentmain(String arg, Instrumentation inst) {
        Thread t = new Thread(new InstrumentationRemoteExecutionControl(inst, arg), "rjshell");
        t.setDaemon(false);
        t.start();
    }
}
