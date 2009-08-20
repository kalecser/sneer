package sneer.bricks.software.bricks.compiler.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.software.bricks.compiler.BrickCompiler;
import sneer.bricks.software.bricks.compiler.BrickCompilerException;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.foundation.brickness.Brick;
import sneer.foundation.brickness.ClassDefinition;
import sneer.foundation.brickness.Nature;
import sneer.foundation.brickness.testsupport.BrickTest;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;

public class BrickCompilerTest extends BrickTest {
	
	private final BrickCompiler _subject = my(BrickCompiler.class);
	
	@Before
	public void prepareFolders() throws Exception {
		srcFolder().mkdirs();
		binFolder().mkdirs();
		
		copyRequiredFoundationSourceFiles();
	}

	@Test
	public void brickDependency() throws Exception {
		
		writeSourceFile("bricks/a/A.java",
			"package bricks.a;" +
			"@" + Brick.class.getName() + " " +
			"public interface A {}");
		
		writeSourceFile("bricks/a/impl/AImpl.java",
				"package bricks.a.impl;" +
				"import static " + Environments.class.getName() + ".my; " +
				"class AImpl implements bricks.a.A {" +
					"{ my(bricks.b.B.class).foo(); }" +
				"}");
		
		writeSourceFile("bricks/b/B.java", "" +
				"package bricks.b;" +
				"@" + Brick.class.getName() + " " +
				"public interface B {" +
					"void foo();" +
				"}");
		
		_subject.compile(srcFolder(), binFolder());
		
		assertBinFilesExist(
			"bricks/a/A.class",
			"bricks/a/impl/AImpl.class",
			"bricks/b/B.class");
		
	}
	
	@Ignore
	@Test(expected=BrickCompilerException.class)
	public void illegalDependency() throws Exception {
		
		writeSourceFile("bricks/a/A.java",
			"package bricks.a;" +
			"@" + Brick.class.getName() + " " +
			"public interface A {}");
		
		writeSourceFile("bricks/a/impl/AImpl.java",
				"package bricks.a.impl;" +
				"public class AImpl implements bricks.a.A {" +
				
					// ILLEGAL DEPENDENCY ON IMPL CLASS
					"{ bricks.b.impl.BImpl.foo(); }" +
					
				"}");
		
		writeSourceFile("bricks/b/impl/BImpl.java", "" +
				"package bricks.b.impl;" +
				"public class BImpl { public static void foo() {} }");
		
		_subject.compile(srcFolder(), binFolder());
	}
	
	private void assertBinFilesExist(String... filenames) {
		for (String filename : filenames)
			assertExists(new File(binFolder(), filename));
	}

	
	private void copyRequiredFoundationSourceFiles() throws IOException {
		
		copyPlatformSourceFiles(
				Brick.class,
				Nature.class,
				ClassDefinition.class,
				
				Environment.class,
				Environments.class,
				Closure.class);
		
	}

	private void copyPlatformSourceFiles(Class<?>... classes) throws IOException {
		for (Class<?> c : classes) copyPlatformSourceFile(c);
	}

	private void copyPlatformSourceFile(Class<?> clazz) throws IOException {
		String sourceFile = clazz.getName().replace('.', '/') + ".java";
		my(IO.class).files().copyFile(new File(platformSrc(), sourceFile), new File(srcFolder(), sourceFile));
	}

	private File platformSrc() {
		return new File(platformBin().getParentFile(), "src");
	}

	private File platformBin() {
		return my(ClassUtils.class).classpathRootFor(Brick.class);
	}

	private void writeSourceFile(String filename, String data) throws IOException {
		my(IO.class).files().writeString(new File(srcFolder(), filename), data);
	}

	private File binFolder() {
		return new File(tmpFolder(), "bin");
	}

	private File srcFolder() {
		return new File(tmpFolder(), "src");
	}

}
