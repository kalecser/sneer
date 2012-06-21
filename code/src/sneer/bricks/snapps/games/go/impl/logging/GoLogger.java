package sneer.bricks.snapps.games.go.impl.logging;


public class GoLogger {

	private static final boolean DEBUG = false;
	
	public static void log(Object log){
		if(DEBUG){
			System.out.println(log);
		}
	}
	
}
