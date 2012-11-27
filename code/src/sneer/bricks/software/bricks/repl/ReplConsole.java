package sneer.bricks.software.bricks.repl;


public interface ReplConsole {
	
	public static final String RESULT_PREFIX = "\n --> ";

	String eval(String code);

}
