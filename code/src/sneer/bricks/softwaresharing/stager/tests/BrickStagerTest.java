package sneer.bricks.softwaresharing.stager.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.ram.collections.CollectionUtils;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.code.compilers.java.JavaCompiler;
import sneer.bricks.software.code.java.source.writer.JavaSourceWriter;
import sneer.bricks.software.code.java.source.writer.JavaSourceWriters;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.bricks.softwaresharing.BrickHistory;
import sneer.bricks.softwaresharing.BrickSpace;
import sneer.bricks.softwaresharing.stager.BrickStager;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Predicate;
import sneer.foundation.testsupport.AssertUtils;

public class BrickStagerTest extends BrickTestBase {

	{
		my(JavaCompiler.class);
		
		my(FolderConfig.class).stageFolder().set(newTmpFile("stage"));
		my(FolderConfig.class).srcFolder().set(newTmpFile("src"));
	}
	
	final BrickStager _subject = my(BrickStager.class);
	
	
	@Before
	public void beforeBrickStagerTest() throws IOException {
		copyBrickBaseToSrcFolder();
	}


	static public void copyBrickBaseToSrcFolder() throws IOException {
		srcFolder().mkdirs();
		
		copyClassesToSrcFolder(
			sneer.foundation.brickness.Brick.class,
			sneer.foundation.brickness.Nature.class,
			sneer.foundation.brickness.ClassDefinition.class,
			
			sneer.foundation.environments.Environment.class,
			sneer.foundation.environments.Environments.class);
	}
	
	
	@Test (timeout = 6000)
	public void stagingFailureIsReportedAsBlinkingLight() throws Throwable {
		prepareBrickY();
		
		assertTrue(srcFileFor(Brick.class).delete());
		
		assertEquals(0, errorLights().size());
		_subject.stageBricksForInstallation();
		assertTrue(errorLights().size() > 0);
	}
	
	@Test (timeout = 6000)
	public void stagingFailureRollsBackStage() throws Throwable {
		prepareBrickY();
		assertTrue(srcFileFor(Brick.class).delete());
		_subject.stageBricksForInstallation();
		assertFalse(stageFolder().exists());
	}

	
	private Collection<Light> errorLights() {
		return my(CollectionUtils.class).filter(blinkingLights(), new Predicate<Light>() { @Override public boolean evaluate(Light light) {
			return light.type() == LightType.ERROR;
		}});
	}


	private List<Light> blinkingLights() {
		return my(BlinkingLights.class).lights().currentElements();
	}

	
	@Test (timeout = 6000)
	public void stageOneBrick() throws Exception  {
		prepareBrickY();
		
		_subject.stageBricksForInstallation();

		assertEquals(0, errorLights().size());
		
		assertStagedFilesExist(
			"src/sneer/foundation/brickness/Brick.java",
			"bin/sneer/foundation/brickness/Brick.class",
			
			"src/bricks/y/Y.java",
			"src/bricks/y/impl/YImpl.java",
			"bin/bricks/y/Y.class",
			"bin/bricks/y/impl/YImpl.class"
		);
		
		File original = new File(srcFolder(), "bricks/y");
		File staged = stagedFile("src/bricks/y");
		assertSameContents(original, staged);
	}
	
	@Test (timeout = 6000)
	public void stageBrickWithJUnitTests() throws Exception  {
		
		copyJUnitJarToSrcFolder();
		
		generateBrickY().write("bricks.y.tests.YTest", "import org.junit.Test; class YTest {}");
		chooseYForExecution();
		
		_subject.stageBricksForInstallation();

		if (errorLights().size() > 0)
			fail(errorLights().toString());
		assertStagedFilesExist("bin/bricks/y/tests/YTest.class");
	}

	private void copyJUnitJarToSrcFolder() throws IOException {
		File junitJar = new File(repositorySrcFileFor(AssertUtils.class).getParentFile(), "lib/junit-4.4.jar");
		copyFile(junitJar, new File(srcFileFor(AssertUtils.class).getParentFile(), "lib/junit-4.4.jar"));
	}

	
	private void prepareBrickY() throws IOException {
		generateBrickY();
		chooseYForExecution();
	}


	private void chooseYForExecution() {
		BrickHistory Y = waitForAvailableBrickY();
		Y.setChosenForExecution(single(Y.versions()), true);
	}


	private BrickHistory waitForAvailableBrickY() {
		my(Logger.class).log("Starting discovery of local bricks...");
		my(BrickSpace.class);
		
		waitForAvailableBrick("bricks.y.Y");
		
		return single(my(BrickSpace.class).availableBricks());
	}


	private JavaSourceWriter generateBrickY() throws IOException {
		JavaSourceWriter writer = newJavaSourceWriter();
		writer.write("bricks.y.Y", "@" + Brick.class.getName() + " public interface Y {}");
		writer.write("bricks.y.impl.YImpl", "class YImpl implements bricks.y.Y {}");
		return writer;
	}


	private JavaSourceWriter newJavaSourceWriter() {
		return srcWriterFor(srcFolder());
	}

	
	private static ClassUtils classUtils() {
		return my(ClassUtils.class);
	}
	
	
	private static File srcFolder() { return my(FolderConfig.class).srcFolder().get(); }
	private File stageFolder() { return my(FolderConfig.class).stageFolder().get(); }

	
	private void assertStagedFilesExist(String... fileNames) {
		for (String fileName : fileNames) assertStagedFileExists(fileName);
	}


	private void assertStagedFileExists(String fileName) {
		assertExists(stagedFile(fileName));
	}
	
	
	private File stagedFile(String fileName) {
		return new File(stageFolder(), fileName);
	}


	public static void copyClassesToSrcFolder(Class<?>... classes) throws IOException {
		for (Class<?> c : classes)
			copyClassToSrcFolder(c);
	}


	private static void copyClassToSrcFolder(final Class<?> clazz) throws IOException {
		copyFile(repositorySrcFileFor(clazz), srcFileFor(clazz));
	}


	private static void copyFile(File from, File to) throws IOException {
		my(IO.class).files().copyFile(from, to);
	}

	
	private static File srcFileFor(final Class<?> clazz) {
		return javaFileNameAt(srcFolder(), clazz);
	}

	
	private static File javaFileNameAt(File rootFolder, Class<?> clazz) {
		return new File(rootFolder, classUtils().relativeJavaFileName(clazz));
	}
	
	
	private static File repositorySrcFileFor(final Class<?> clazz) {
		return new File(repositorySrcFolder(), classUtils().relativeJavaFileName(clazz));
	}
	
	
	private static File repositorySrcFolder() {
		return new File(repositoryBinFolder().getParentFile(), "src");
	}

	
	private static File repositoryBinFolder() {
		return classUtils().classpathRootFor(Brick.class);
	}

	
	private <T> T single(Collection<T> collection) {
		assertEquals(1, collection.size());
		return collection.iterator().next();
	}


	private void waitForAvailableBrick(final String brickName) {
		final Latch latch = my(Latches.class).produce();
		
		WeakContract contract = my(BrickSpace.class).newBuildingFound().addReceiver(new Consumer<Seal>() { @Override public void consume(Seal publisher) {
			if (isBrickAvailable(brickName)) latch.open();
		}});
		if (isBrickAvailable(brickName)) latch.open();

		latch.waitTillOpen();
		contract.dispose();
	}

	
	private boolean isBrickAvailable(final String brickName) {
		for (BrickHistory brickInfo : my(BrickSpace.class).availableBricks())
			if (brickInfo.name().equals(brickName))
				return true;
		
		return false;
	}

	
	private JavaSourceWriter srcWriterFor(File srcFolder) {
		return my(JavaSourceWriters.class).newInstance(srcFolder);
	}
	

	private void assertSameContents(File folder1, File folder2)	throws IOException {
		my(IO.class).files().assertSameContents(folder1, folder2);
	}

}
