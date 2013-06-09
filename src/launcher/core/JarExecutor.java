package launcher.core;

import java.io.IOException;

public class JarExecutor {
	/*
	 * JarExecutor.Java
	 * Capable of executing a .JAR via java code ^_^
	 * Author: GabrielBailey74
	 */
	private static final Runtime RUNTIME = Runtime.getRuntime();
	private String recentArgument;

	public JarExecutor(String jarName, String[] args) {
		String array2Str = null;
		if (args != null && args.length > 0) {
			array2Str = array2String(args); // pass the arguments to the .jar if there's any
		}
		recentArgument = "java -jar " + jarName + (array2Str == null ? "" : array2Str);

	}

	/*
	 * Turning a String[] of letters to 1 String();
	 */
	private String array2String(String[] args) {
		String tmp = "";
		for (int i = 0; i < args.length; i++) {
			tmp += " " + args[i];
		}
		return tmp;
	}
	
	/*
	 * System.exit is called here as a custom function to close the JVM
	 * (Rather than having the Java process still run after LodeScapes window is closed)
	 */
	public void execute() throws IOException {
		RUNTIME.exec(recentArgument); // execute the argument on the command line.
		System.exit(0);
	}

}
