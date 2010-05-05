package sneer.bricks.software.code.compilers.java.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import org.junit.Test;

import sneer.bricks.software.code.compilers.CompilationError;
import sneer.bricks.software.code.compilers.Language;
import sneer.bricks.software.code.compilers.LanguageRegistry;
import sneer.bricks.software.code.compilers.Result;
import sneer.bricks.software.code.compilers.tests.LanguageCompilerTestBase;

public class JavaCompilerTest extends LanguageCompilerTestBase {

	@Override
	protected Language language() {
		return my(LanguageRegistry.class).languageByFileExtension("java");
	}

	@Override
	@Test
	public void doTestBadCode() throws Exception {
		Result result = compile("\nclass \n { public void foo() {} }", null);
		assertFalse(result.success());
		CompilationError error = result.errors().get(0);
		assertEquals(2, error.lineNumber());
		assertEquals("<identifier> expected", error.message());
		assertTrue(new File(error.fileName()).getName().startsWith(TEST_FILE_PREFIX));
	}

}
