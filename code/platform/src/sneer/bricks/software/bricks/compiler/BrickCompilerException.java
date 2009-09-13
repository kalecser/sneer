package sneer.bricks.software.bricks.compiler;

public class BrickCompilerException extends Exception {

	public BrickCompilerException(Exception cause) {
		super(cause);
	}

	public BrickCompilerException(String message) {
		super(message);
	}

}
