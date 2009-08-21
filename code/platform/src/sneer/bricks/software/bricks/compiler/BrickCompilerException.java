package sneer.bricks.software.bricks.compiler;

public class BrickCompilerException extends RuntimeException {

	public BrickCompilerException(Exception cause) {
		super(cause);
	}

	public BrickCompilerException(String message) {
		super(message);
	}

}
