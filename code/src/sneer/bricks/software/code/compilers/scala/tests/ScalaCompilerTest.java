package sneer.bricks.software.code.compilers.scala.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import org.junit.Test;

import sneer.bricks.software.code.compilers.CompilationError;
import sneer.bricks.software.code.compilers.Language;
import sneer.bricks.software.code.compilers.LanguageRegistry;
import sneer.bricks.software.code.compilers.Result;
import sneer.bricks.software.code.compilers.tests.LanguageCompilerTest;

public class ScalaCompilerTest extends LanguageCompilerTest {

	@Override
	protected Language language() {
		return my(LanguageRegistry.class).languageByFileExtension("scala");
	}

	@Override
	@Test
	public void testBadCode() throws Exception {
		Result result = compile("\nclass \n { public void foo() {} }", null);
		assertFalse(result.success());
		CompilationError error = result.errors().get(0);
		assertEquals(3, error.lineNumber());
		assertEquals("identifier expected but '{' found.", error.message());
		assertTrue(new File(error.fileName()).getName().startsWith(TEST_FILE_PREFIX));
	}

}
