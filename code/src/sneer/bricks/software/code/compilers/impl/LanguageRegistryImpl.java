package sneer.bricks.software.code.compilers.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.software.code.compilers.Language;
import sneer.bricks.software.code.compilers.LanguageRegistry;
import sneer.bricks.software.code.compilers.java.JavaCompiler;
import sneer.bricks.software.code.compilers.scala.ScalaCompiler;

class LanguageRegistryImpl implements LanguageRegistry {

	private final ListRegister<Language> _languages = my(CollectionSignals.class).newListRegister();

	{
		_languages.add(new LanguageImpl("java", my(JavaCompiler.class)));
		_languages.add(new LanguageImpl("scala", my(ScalaCompiler.class)));
	}
	
	@Override
	public ListSignal<Language> languages() {
		return _languages.output();
	}

	@Override
	public Language languageByFileExtension(String fileExtension) {
		for (Language language : languages())
			if(language.fileExtension().equals(fileExtension)) 
				return language;
		return null;
	}

}
