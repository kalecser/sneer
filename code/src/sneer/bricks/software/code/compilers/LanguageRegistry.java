package sneer.bricks.software.code.compilers;

import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.foundation.brickness.Brick;

@Brick
public interface LanguageRegistry {

	ListSignal<Language> languages();

	Language languageByFileExtension(String fileExtension);

}
