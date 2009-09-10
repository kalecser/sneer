package sneer.bricks.software.bricks.compiler.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.software.bricks.compiler.BrickCompilerException;
import sneer.bricks.software.bricks.compiler.Builder;
import sneer.bricks.software.bricks.compiler.tests.fixtures.Foo;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.code.jar.JarBuilder;
import sneer.bricks.software.code.jar.Jars;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.Brick;
import sneer.foundation.brickness.ClassDefinition;
import sneer.foundation.brickness.Nature;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;

public class BuilderTest extends BrickTest {
	
	private final Builder _subject = my(Builder.class);
	
	@Before
	public void prepareFolders() throws Exception {
		
		my(FolderConfig.class).platformSrcFolder().set(srcFolder());
		
		srcFolder().mkdirs();
		binFolder().mkdirs();
		
		copyRequiredFoundationFiles();
	}
	
	@Test
	public void foundationLibs() throws IOException {
		writeSourceFile("sneer/foundation/Bar.java",
				"package sneer.foundation;" +
				"class Bar {" +
					"{ " + Foo.class.getName() + ".bar(); }" +
				"}");
		
		writeLib("sneer/foundation/foo.jar", Foo.class);
		
		_subject.build(srcFolder(), binFolder());
		
		assertBinFilesExist(
			"sneer/foundation/Bar.class",
			"sneer/foundation/foo.jar");
	}
	
	@Ignore
	@Test
	public void testsCanDependOnFoundationLibs() throws IOException {
		writeSourceFile("bricks/a/A.java",
				"package bricks.a;" +
				"@" + Brick.class.getName() + " " +
				"public interface A {}");
			
		writeSourceFile("bricks/a/tests/ATest.java",
				"package bricks.a.tests;" +
				"class ATest {" +
					"{ " + Foo.class.getName() + ".bar(); }" +
				"}");
		
		writeLib("sneer/foundation/foo.jar", Foo.class);
		
		_subject.build(srcFolder(), binFolder());
		
		assertBinFilesExist(
			"bricks/a/A.class",
			"bricks/a/tests/ATest.class",
			"sneer/foundation/foo.jar");
	}
	
	@Test
	public void libDependencies() throws IOException {
		
		
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
		
		_subject.build(srcFolder(), binFolder());
		
		assertBinFilesExist(
			"bricks/a/A.class",
			"bricks/a/impl/AImpl.class",
			"bricks/a/impl/lib/foo.jar");
	}

	private void writeLib(String filename, Class<Foo> clazz) throws IOException {
		JarBuilder builder = my(Jars.class).builder(srcFile(filename));
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
		
		_subject.build(srcFolder(), binFolder());
		
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
		
		_subject.build(srcFolder(), binFolder());
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
		
		_subject.build(srcFolder(), binFolder());
	}
	
	private File platformBin() {
		return my(ClassUtils.class).classpathRootFor(Brick.class);
	}
	
	private void assertBinFilesExist(String... filenames) {
		for (String filename : filenames)
			assertExists(new File(binFolder(), filename));
	}
	
	private void copyRequiredFoundationFiles() throws IOException {
		
		copySourceFiles(
				Brick.class,
				Nature.class,
				ClassDefinition.class,
				
				Environment.class,
				Environments.class,
				Closure.class);
		
	}

	private void copySourceFiles(Class<?>... classes) throws IOException {
		for (Class<?> c : classes) copySourceFile(c);
	}

	private void copySourceFile(Class<?> clazz) throws IOException {
		copyFile(
				platformSourceFileFor(clazz),
				srcFile(relativeJavaFileName(clazz)));
	}

	private String relativeJavaFileName(Class<?> clazz) {
		return my(ClassUtils.class).relativeJavaFileName(clazz);
	}

	private void copyFile(File from, File to) throws IOException {
		my(IO.class).files().copyFile(from, to);
	}

	private File platformSourceFileFor(Class<?> clazz) {
		return new File(platformSrcFolder(), relativeJavaFileName(clazz));
	}

	private File platformSrcFolder() {
		return new File(platformBin().getParentFile(), "src");
	}

	private void writeSourceFile(String filename, String data) throws IOException {
		my(IO.class).files().writeString(srcFile(filename), data);
	}

	private File srcFile(String filename) {
		return new File(srcFolder(), filename);
	}

	private File binFolder() {
		return new File(tmpFolder(), "bin");
	}

	private File srcFolder() {
		return new File(tmpFolder(), "src");
	}

}
