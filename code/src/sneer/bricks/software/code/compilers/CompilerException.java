package sneer.bricks.software.code.compilers;

public class CompilerException extends Exception {

	private final Result _result;
	
	public CompilerException(Result result) {
		super(result.errorString());
		_result = result;
	}

	public Result result() {
		return _result;
	}

}