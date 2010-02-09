package sneer.bricks.software.code.compilers.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.software.code.compilers.Language;
import sneer.bricks.software.code.compilers.LanguageCompiler;
import sneer.bricks.software.code.compilers.LanguageRegistry;
import sneer.bricks.software.code.compilers.java.JavaCompiler;
import sneer.bricks.software.code.compilers.scala.ScalaCompiler;
import sneer.bricks.software.folderconfig.tests.BrickTest;

public class LanguageRegistryTest extends BrickTest {

	private static final String JAVA_FILE_EXTENSION = "java";
	private static final String SCALA_FILE_EXTENSION = "scala";

	@Test
	public void defaultLanguages() {
		ListSignal<Language> languages = my(LanguageRegistry.class).languages();
		assertEquals(2, languages.size().currentValue().intValue());
		assertLanguage(languages.currentGet(0), JAVA_FILE_EXTENSION, JavaCompiler.class);
		assertLanguage(languages.currentGet(1), SCALA_FILE_EXTENSION, ScalaCompiler.class);
	}

	private void assertLanguage(Language language, String expectedFileExtension, Class<? extends LanguageCompiler> expectedCompilerClass) {
		assertEquals(expectedFileExtension, language.fileExtension());
		assertSame(my(expectedCompilerClass), language.compiler());
	}
	
	@Test
	public void languageByName() {
		assertSame(my(JavaCompiler.class), my(LanguageRegistry.class).languageByFileExtension(JAVA_FILE_EXTENSION).compiler());
	}
}
