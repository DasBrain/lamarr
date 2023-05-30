# lamarr
Attach JShell to an existing Java Process

## Usage

    jshell -J--module-path=lamarr-1.0.jar --execution instrumentation:pid(128)

Replace `128` with the target process id (pid).  
You may want to add `--module-path <module-path> --add-modules <roots>` and/or `-cp <classpath>` of your application.

## How it works

Lamarr loads a java agent into the target process, and gives JShell an [`ExecutionControl`](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.jshell/jdk/jshell/spi/ExecutionControl.html) that uses that process.

## Limitations

* Because lamarr does not use the Java Debug Interface (JDI) - unlike the usualy used `ExecutionControl`, it **can't interrupt snippets**.  
  Because it is easy to accidentially enter a runaway snippet, **usage in production is discouraged**.  
  If it does happen anyway, you have to `kill` the target process.
* Lamarr does not redirect `System.in`, `System.out` and `System.err`.  
  This is intentional. But this also means you can't use `System.out` to get the result into your JShell.
* Lamarr does automatically configure the classpath/modulepath for you.  
  You have to do it by hand.
  
## Background on how JShell works

Roughtly speaking, JShell consits of 3 parts:

* Frontend
* Compiler (javac)
* ExecutionControl

Lamarr only provides an ExecutionControl to JShell.  
As such, it will recive a few commands, such as "install bytecode", "execute a method"... and sends them to the java agent, which will do that in the target VM.

Notably, without manual setup the compiler does not know about any application specific classes.

The bytecode is installed into their own `ClassLoader`, the parent loader is the [system class loader](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/ClassLoader.html#getSystemClassLoader%28%29).


