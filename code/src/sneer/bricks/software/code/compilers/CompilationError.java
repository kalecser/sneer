package sneer.bricks.software.code.compilers;

public interface CompilationError {

	int getLineNumber();

	String getMessage();

	String getFileName();

}