package sneer.bricks.snapps.games.go.impl.logging;


public class GoLogger {

	private static final boolean DEBUG = true;
	
	public static void log(String log){
		if(!DEBUG) return;
		System.out.println(log);
	}
	
}
