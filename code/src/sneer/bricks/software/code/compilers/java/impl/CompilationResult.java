package sneer.bricks.software.code.compilers.java.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.List;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.software.code.compilers.CompilationError;
import sneer.bricks.software.code.compilers.Result;

class CompilationResult implements Result {

	private int _compilerCode;
	
	private String _errorString;
	
	private List<CompilationError> _errors;
	
	public CompilationResult(int compilerCode) {
		_compilerCode = compilerCode;
	}

	@Override
	public boolean success() {
		return _compilerCode == 0;
	}

	public void setError(String errorString) {
		_errorString = errorString;
	}

	@Override
	public List<CompilationError> errors() {
		if(_errorString != null && _errors == null)
			_errors = parseErrorString(_errorString);

		return _errors;
	}

	private List<CompilationError> parseErrorString(String string) {
		List<CompilationError> result = new ArrayList<CompilationError>();
		List<String> lines = my(Lang.class).strings().readLines(string);
		for (String line : lines) {
			if(line.indexOf(".java") > 0) {
				final String[] parts = my(Lang.class).strings().splitRight(line, ':', 3);
				final String fileName = parts[0];
				final int lineNumber = Integer.parseInt(parts[1]);
				final String message = parts[2];
				result.add(new CompilationErrorImpl(fileName, lineNumber, message));
			}
		}
		return result;
	}

	@Override
	public String errorString() {
		return _errorString;
	}
}