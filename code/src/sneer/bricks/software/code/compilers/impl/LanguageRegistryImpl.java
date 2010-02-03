package sneer.bricks.software.code.compilers.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.software.code.compilers.Language;
import sneer.bricks.software.code.compilers.LanguageRegistry;
import sneer.bricks.software.code.compilers.java.JavaCompiler;

class LanguageRegistryImpl implements LanguageRegistry {

	private final ListRegister<Language> _languages = my(CollectionSignals.class).newListRegister();

	{
		_languages.add(new LanguageImpl("java", my(JavaCompiler.class)));
	}
	
	@Override
	public ListSignal<Language> languages() {
		return _languages.output();
	}

}
