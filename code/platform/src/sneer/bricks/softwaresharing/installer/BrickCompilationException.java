package sneer.bricks.softwaresharing.installer;


public class BrickCompilationException extends Exception {

	public BrickCompilationException(String compileErrors) {
		super(compileErrors);
	}
	
}
