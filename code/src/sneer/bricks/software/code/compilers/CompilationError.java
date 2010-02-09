package sneer.bricks.software.code.compilers;

public interface CompilationError {

	int lineNumber();

	String message();

	String fileName();

}