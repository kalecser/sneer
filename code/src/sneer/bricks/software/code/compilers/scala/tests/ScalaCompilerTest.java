package sneer.bricks.software.code.compilers.scala.tests;

import static basis.environments.Environments.my;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.software.code.compilers.CompilationError;
import sneer.bricks.software.code.compilers.Language;
import sneer.bricks.software.code.compilers.LanguageRegistry;
import sneer.bricks.software.code.compilers.Result;
import sneer.bricks.software.code.compilers.scala.ScalaCompiler;
import sneer.bricks.software.code.compilers.tests.LanguageCompilerTestBase;

@Ignore
public class ScalaCompilerTest extends LanguageCompilerTestBase {

	@Override
	protected Language language() {
		my(ScalaCompiler.class);
		return my(LanguageRegistry.class).languageByFileExtension("scala");
	}

	@Override
	@Test
	public void doTestBadCode() throws Exception {
		Result result = compile("\nclass \n { public void foo() {} }", null);
		assertFalse(result.success());
		CompilationError error = result.errors().get(0);
		assertEquals(3, error.lineNumber());
		assertEquals("identifier expected but '{' found.", error.message());
		assertTrue(new File(error.fileName()).getName().startsWith(TEST_FILE_PREFIX));
	}

}
