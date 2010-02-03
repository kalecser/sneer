package sneer.bricks.software.code.compilers.java.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.software.code.compilers.CompilationError;

class CompilationErrorImpl implements CompilationError {

	private String _fileName;
	
	private int _lineNumber;
	
	private String _message;
	
	CompilationErrorImpl(String fileName, int lineNumber, String errorMessage) {
		_fileName = fileName;
		_lineNumber = lineNumber;
		_message = my(Lang.class).strings().trimToNull(errorMessage);
	}

	@Override
	public int getLineNumber() {
		return _lineNumber;
	}

	@Override
	public String getMessage() {
		return _message;
	}

	@Override
	public String getFileName() {
		return _fileName;
	}

}
