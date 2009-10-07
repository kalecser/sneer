package sneer.bricks.software.code.compilers.java;


public class JavaCompilerException extends Exception {

	private final Result _result;
	
	public JavaCompilerException(Result result) {
		super(result.getErrorString());
		_result = result;
	}

	public Result result() {
		return _result;
	}

}