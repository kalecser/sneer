package sneer.bricks.software.code.compilers;

import basis.brickness.Brick;
import sneer.bricks.pulp.reactive.collections.ListSignal;

@Brick
public interface LanguageRegistry {

	ListSignal<Language> languages();

	Language languageByFileExtension(String fileExtension);

	void addLanguage(String fileExtension, LanguageCompiler compiler);

}
