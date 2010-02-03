package sneer.bricks.software.code.compilers.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.software.code.compilers.Language;
import sneer.bricks.software.code.compilers.LanguageRegistry;
import sneer.bricks.software.code.compilers.java.JavaCompiler;
import sneer.bricks.software.folderconfig.tests.BrickTest;

public class LanguageRegistryTest extends BrickTest {

	@Test
	public void testLanguages() {
		ListSignal<Language> languages = my(LanguageRegistry.class).languages();
		assertEquals(1, languages.size().currentValue().intValue());
		Language language = languages.currentGet(0);
		assertEquals("java", language.fileExtension());
		assertSame(my(JavaCompiler.class), language.compiler());
	}
}
