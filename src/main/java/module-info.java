module pw.dasbrain.lamarr {
    
    requires transitive java.instrument;
    requires jdk.jshell;
    requires jdk.attach;
    
    exports pw.dasbrain.lamarr.agent to java.instrument;
    opens pw.dasbrain.lamarr.agent to java.instrument;
    
    provides jdk.jshell.spi.ExecutionControlProvider with
        pw.dasbrain.lamarr.InstrumentationExecutionControlProvider;
}