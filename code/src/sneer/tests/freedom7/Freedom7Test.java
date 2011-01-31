package sneer.tests.freedom7;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.software.code.compilers.CompilerException;
import sneer.bricks.software.code.compilers.java.JavaCompiler;
import sneer.bricks.software.code.jar.Jars;
import sneer.bricks.software.code.java.source.writer.JavaSourceWriter;
import sneer.bricks.software.code.java.source.writer.JavaSourceWriters;
import sneer.foundation.brickness.Brick;
import sneer.tests.SovereignFunctionalTestBase;

public class Freedom7Test extends SovereignFunctionalTestBase {

	@Test (timeout = 1000 * 20)
	public void meToo() throws Exception {
		//LoggerMocks.showLog = true;

		a().copyToSourceFolder(generateY());
		
		newSession(a());
		a().enableCodeSharing();
		b().enableCodeSharing();
		
		a().waitForAvailableBrick("freedom7.y.Y", "CURRENT");
		b().waitForAvailableBrick("freedom7.y.Y", "NEW");
		
		b().stageBricksForInstallation("freedom7.y.Y");

		System.clearProperty("freedom7.y.Y.installed");
		newSession(b());
		b().enableCodeSharing();
		b().loadBrick("freedom7.y.Y");
		assertEquals("true", System.getProperty("freedom7.y.Y.installed"));

		b().waitForAvailableBrick("freedom7.y.Y", "CURRENT");
	}

	@Test (timeout = 1000 * 20)
	public void testPublishBrickWithDependencies() throws Exception {
		System.clearProperty("freedom7.w.W.installed");
		copyToSourceFolderAndStageForInstallation(new File[] { generateY(), generateW() }, "freedom7.y.Y", "freedom7.w.W");
		assertEquals("true", System.getProperty("freedom7.w.W.installed"));
	}
	
	@Test (timeout = 1000 * 20)
	public void testPublishBrickWithLib() throws Exception {
		System.clearProperty("freedom7.lib.Lib.executed");
		copyToSourceFolderAndStageForInstallation(new File[] { generateX() }, "freedom7.x.X");
		assertEquals("true", System.getProperty("freedom7.lib.Lib.executed"));
	}

	private void copyToSourceFolderAndStageForInstallation(File[] folders, String... brickNames) throws IOException, CompilerException {
		for (File f : folders) a().copyToSourceFolder(f);		
		a().enableCodeSharing();
		for (String brickName : brickNames) a().waitForAvailableBrick(brickName, "CURRENT");
		a().stageBricksForInstallation(brickNames);
		newSession(a());
		for (String brickName : brickNames) a().loadBrick(brickName);
	}
	
	@Test
	@Ignore
	public void testIndirectDependencies() {
		fail("indirect dependencies break compareTo");
	}
	
	@Test
	@Ignore
	public void testIndirectCycles() {
		fail("indirect cycles, what's the doubt?");
	}
	
	private File generateX() throws Exception {
		
		final File src = sourceFolder("x");
		
		generateLib(new File(src, "freedom7/x/impl/lib/lib.jar"));
		
		final JavaSourceWriter writer = javaSourceWriterFor(src);
		writer.write("freedom7.x.X",
				"@sneer.foundation.brickness.Brick public interface X {}");
		writer.write("freedom7.x.impl.XImpl",
				"class XImpl implements freedom7.x.X {\n" +
					"public XImpl() {\n" +
						"freedom7.lib.Lib.execute();\n" +
					"}" +
				"}");	
		return src;
	}

	private void generateLib(final File libJar) throws Exception {
		final File lib = sourceFolder("lib");
		final File sourceFile = javaSourceWriterFor(lib).write("freedom7.lib.Lib",
			"public class Lib {" +
				"public static void execute() {" +
					"System.setProperty(\"freedom7.lib.Lib.executed\", \"true\");" +
				"}" +
			"}");
		
		File outputFolder = sourceFolder("bin");
		outputFolder.mkdirs();
		
		my(JavaCompiler.class).compile(Arrays.asList(sourceFile), outputFolder);
		my(Jars.class).build(libJar, outputFolder);
	}

	private File generateY() throws IOException {
		File src = sourceFolder("src-y");
		writeY(src);
		return src;
	}
	
	private File sourceFolder(String sourceFolder) {
		return new File(tmpFolder(), sourceFolder);
	}

	private void writeY(File srcFolder) throws IOException {
		JavaSourceWriter writer = javaSourceWriterFor(srcFolder);
		writer.write("freedom7.y.Y",
				"@" + Brick.class.getName() + " " +
				"public interface Y {}");
		writer.write("freedom7.y.impl.YImpl",
				"class YImpl implements freedom7.y.Y {\n" +
					"public YImpl() {\n" +
						"System.setProperty(\"freedom7.y.Y.installed\", \"true\");\n" +
					"}" +
				"}");
	}


	private JavaSourceWriter javaSourceWriterFor(File srcFolder) {
		return my(JavaSourceWriters.class).newInstance(srcFolder);
	}	

	private File generateW() throws IOException {
		final File src = sourceFolder("src-w");
		writeW(javaSourceWriterFor(src));
		return src;
	}

	private void writeW(final JavaSourceWriter writer) throws IOException {
		writer.write("freedom7.w.W",
				"@sneer.foundation.brickness.Brick\n" +
				"public interface W {\n" +
					"freedom7.y.Y y();\n" +
				"}");
		writer.write("freedom7.w.impl.WImpl",
				"import static " + sneer.foundation.environments.Environments.class.getName() + ".my;\n" +
				"class WImpl implements freedom7.w.W {\n" +
					"private freedom7.y.Y _y = my(freedom7.y.Y.class);\n" +
					"public WImpl() {\n" +
						"if (_y == null) throw new IllegalStateException();\n" +
						"System.setProperty(\"freedom7.w.W.installed\", \"true\");\n" +
					"}" +
					"public freedom7.y.Y y() {\n" +
						"return _y;\n" +
					"}\n" +
				"}");
	}
}