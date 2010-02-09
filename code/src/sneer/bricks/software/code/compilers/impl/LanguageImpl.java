package sneer.bricks.software.code.compilers.impl;

import sneer.bricks.software.code.compilers.Language;
import sneer.bricks.software.code.compilers.LanguageCompiler;

public class LanguageImpl implements Language {

	private final String _fileExtension;
	private final LanguageCompiler _compiler;

	public LanguageImpl(String fileExtension, LanguageCompiler compiler) {
		_fileExtension = fileExtension;
		_compiler = compiler;
	}

	@Override
	public String fileExtension() {
		return _fileExtension;
	}

	@Override
	public LanguageCompiler compiler() {
		return _compiler;
	}

}
