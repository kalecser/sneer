package sneer.bricks.software.code.compilers.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.software.code.compilers.Language;
import sneer.bricks.software.code.compilers.LanguageCompiler;
import sneer.bricks.software.code.compilers.LanguageRegistry;

class LanguageRegistryImpl implements LanguageRegistry {

	private final ListRegister<Language> _languages = my(CollectionSignals.class).newListRegister();

	
	@Override
	public ListSignal<Language> languages() {
		return _languages.output();
	}

	
	@Override
	public Language languageByFileExtension(String fileExtension) {
		for (Language language : languages())
			if (language.fileExtension().equals(fileExtension))
				return language;
		return null;
	}

	
	@Override
	public void addLanguage(String fileExtension, LanguageCompiler compiler) {
		_languages.add(new LanguageImpl(fileExtension, compiler));
	}

}
