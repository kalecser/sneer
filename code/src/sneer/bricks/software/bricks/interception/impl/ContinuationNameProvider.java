package sneer.bricks.software.bricks.interception.impl;


public class ContinuationNameProvider {

	private static int _continuations;
	
	static String continuationNameFor(String methodName) {
		return methodName + "$" + (++_continuations);
	}

}
