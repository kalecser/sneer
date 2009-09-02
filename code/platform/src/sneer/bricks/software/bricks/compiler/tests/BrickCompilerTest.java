package sneer.bricks.software.bricks.compiler.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.software.bricks.compiler.BrickCompiler;
import sneer.bricks.software.bricks.compiler.BrickCompilerException;
import sneer.bricks.software.bricks.compiler.tests.fixtures.Foo;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.code.jar.JarBuilder;
import sneer.bricks.software.code.jar.Jars;
import sneer.bricks.software.folderconfig.FolderConfig;
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
		
		copyRequiredFoundationFiles();
	}
	
	@Test
	public void libizinha() throws IOException {
		
		my(FolderConfig.class).platformBinFolder().set(my(ClassUtils.class).classpathRootFor(Brick.class));
		
		writeSourceFile("bricks/a/A.java",
				"package bricks.a;" +
				"@" + Brick.class.getName() + " " +
				"public interface A {}");
			
		writeSourceFile("bricks/a/impl/AImpl.java",
				"package bricks.a.impl;" +
				"class AImpl implements bricks.a.A {" +
					"{ " + Foo.class.getName() + ".bar(); }" +
				"}");
		
		writeLib("bricks/a/impl/lib/foo.jar", Foo.class);
		
		_subject.compile(srcFolder(), binFolder());
		
		assertBinFilesExist(
			"bricks/a/A.class",
			"bricks/a/impl/AImpl.class",
			"bricks/a/impl/lib/foo.jar");
	}

	private void writeLib(String filename, Class<Foo> clazz) throws IOException {
		JarBuilder builder = my(Jars.class).builder(sourceFile(filename));
		builder.add(my(ClassUtils.class).relativeClassFileName(clazz), my(ClassUtils.class).classFile(clazz));
		builder.close();
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
	
	@Test(expected=BrickCompilerException.class)
	public void illegalApiDependency() throws Exception {
		
		writeSourceFile("bricks/a/A.java",
			"package bricks.a;" +
			"@" + Brick.class.getName() + " " +
			"public interface A { Foo foo(); }");
		
		writeSourceFile("bricks/a/impl/Foo.java",
				"package bricks.a.impl;" +
				"public class Foo {}");
		
		_subject.compile(srcFolder(), binFolder());
	}
	
	private void assertBinFilesExist(String... filenames) {
		for (String filename : filenames)
			assertExists(new File(binFolder(), filename));
	}
	
	private void copyRequiredFoundationFiles() throws IOException {
		
		copyClassFilesToBin(
				Brick.class,
				Nature.class,
				ClassDefinition.class,
				
				Environment.class,
				Environments.class,
				Closure.class);
		
	}

	private void copyClassFilesToBin(Class<?>... classes) throws IOException {
		for (Class<?> c : classes) copyClassFileToBin(c);
	}

	private void copyClassFileToBin(Class<?> clazz) throws IOException {
		copyFile(
				toFile(clazz),
				new File(binFolder(), toRelativeFileName(clazz)));
	}

	private String toRelativeFileName(Class<?> clazz) {
		return my(ClassUtils.class).relativeClassFileName(clazz);
	}

	private void copyFile(File from, File to) throws IOException {
		my(IO.class).files().copyFile(from, to);
	}

	private File toFile(Class<?> clazz) {
		return my(ClassUtils.class).classFile(clazz);
	}

	private void writeSourceFile(String filename, String data) throws IOException {
		my(IO.class).files().writeString(sourceFile(filename), data);
	}

	private File sourceFile(String filename) {
		return new File(srcFolder(), filename);
	}

	private File binFolder() {
		return new File(tmpFolder(), "bin");
	}

	private File srcFolder() {
		return new File(tmpFolder(), "src");
	}

}
