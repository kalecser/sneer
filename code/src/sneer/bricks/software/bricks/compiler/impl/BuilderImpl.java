package sneer.bricks.software.bricks.compiler.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.software.bricks.compiler.BrickCompilerException;
import sneer.bricks.software.bricks.compiler.Builder;
import sneer.bricks.software.code.compilers.Language;
import sneer.bricks.software.code.compilers.LanguageRegistry;

class BuilderImpl implements Builder {

	@Override
	public void build(File srcFolder, File destFolder) throws IOException, BrickCompilerException {
		ListSignal<Language> languages = my(LanguageRegistry.class).languages();

		if(languages.currentElements().isEmpty())
			throw new IllegalStateException("mute - no languages found");

		for (Language language : languages)
			new Build(srcFolder, destFolder, language);
	}
	
}
