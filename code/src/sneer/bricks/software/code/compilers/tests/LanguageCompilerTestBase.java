package sneer.bricks.software.code.compilers.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;

import org.junit.Test;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.software.code.compilers.CompilerException;
import sneer.bricks.software.code.compilers.Language;
import sneer.bricks.software.code.compilers.LanguageCompiler;
import sneer.bricks.software.code.compilers.Result;
import sneer.bricks.software.code.compilers.java.tests.JarUtils;
import sneer.bricks.software.code.compilers.java.tests.TestLib;
import sneer.bricks.software.folderconfig.tests.BrickTest;

public abstract class LanguageCompilerTestBase extends BrickTest {

	protected static final String TEST_FILE_PREFIX = "sneer-test-";

	protected abstract Language language();


	@Test
	public void testCompile() throws Exception {
		Result result = compile("class Foo {}", null);
		assertSuccess(result);
	}

	@Test
	public void testBadCode() throws Exception {
		doTestBadCode();
	};

	protected abstract void doTestBadCode() throws Exception;


	@Test
	public void testEmptyDir() throws Exception {
		Result result = compile("bricks/compiler/test-resources/empty", null);
		assertFalse(result.success());
	}

	@Test
	public void testWithExternalDependencies() throws Exception {
		try {
			tryToTestWithExternalDependencies();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void tryToTestWithExternalDependencies() throws IOException,
			URISyntaxException {
		final File libFolder = createLibFolder();
		JarUtils.createJar(new File(libFolder, "lib.jar"), TestLib.class);
		Result result = compile("class Foo extends " + TestLib.class.getName() + " {}", libFolder);
		assertSuccess(result);
	}

	private void assertSuccess(Result result) {
		assertTrue(result.errorString(), result.success());
	}

	private File createLibFolder() {
		final File dir = new File(tmpFolder(), "lib");
		dir.mkdirs();
		return dir;
	}

	protected Result compile(String code, File libDir) throws IOException {
		File java = writeSourceFile(code);
		File[] classpath = classPathForLibs(libDir);
		try {
			return compiler().compile(Collections.singletonList(java), tmpFolder(), classpath);
		} catch (CompilerException e) {
			return e.result();
		}
	}

	private File writeSourceFile(String code) {
		try {
			File java = createTempFile(); 
			my(IO.class).files().writeString(java, code);
			return java;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		
	}

	private File createTempFile() throws IOException {
		return createTmpFile(TEST_FILE_PREFIX + fileExtension());
	}

	private File[] classPathForLibs(File libDir) {
		return libDir == null
			? new File[0]
			: listJarFiles(libDir);
	}

	private File[] listJarFiles(File libDir) {
		return libDir.listFiles(new FilenameFilter() { @Override public boolean accept(File dir, String name) {
			return name.endsWith(".jar");
		}});
	}

	private String fileExtension() {
		return "." + language().fileExtension();
	}

	private LanguageCompiler compiler() {
		return language().compiler();
	}

}