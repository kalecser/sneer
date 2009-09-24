package sneer.bricks.softwaresharing.installer.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.code.java.source.writer.JavaSourceWriter;
import sneer.bricks.software.code.java.source.writer.JavaSourceWriters;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.bricks.softwaresharing.BrickInfo;
import sneer.bricks.softwaresharing.BrickSpace;
import sneer.bricks.softwaresharing.installer.BrickInstaller;
import sneer.foundation.brickness.Brick;
import sneer.foundation.brickness.Seal;
import sneer.foundation.lang.Consumer;


public class BrickInstallerTest extends BrickTest {

	
	final BrickInstaller _subject = my(BrickInstaller.class);
	
	
	@Before
	public void setUpSrc() throws IOException {
		srcFolder().mkdirs();
		
		my(FolderConfig.class).platformSrcFolder().set(srcFolder());
		
		copyClassesToSrcFolder(
			sneer.foundation.brickness.Brick.class,
			sneer.foundation.brickness.Nature.class,
			sneer.foundation.brickness.ClassDefinition.class);
	}
	
	
	private void copyClassesToSrcFolder(Class<?>... classes) throws IOException {
		for (Class<?> c : classes)
			copyClassToSrcFolder(c);
	}

	
	@Test (timeout = 6000)
	public void stagingFailureIsReportedAsBlinkingLight() throws Throwable {
		stageBrickY();
		
		srcFileFor(Brick.class).delete();
		
		Signal<Integer> size = blinkingLights().size();
		assertEquals(0, size.currentValue().intValue());
		
		_subject.prepareStagedBricksInstallation();
		
		assertEquals(1, size.currentValue().intValue());
	}

	
	private ListSignal<Light> blinkingLights() {
		return my(BlinkingLights.class).lights();
	}
	
	
	@Test (timeout = 6000)
	public void stageOneBrick() throws Exception  {
		stageBrickY();
		
		_subject.prepareStagedBricksInstallation();
		
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

	
	private void stageBrickY() throws IOException {
		JavaSourceWriter writer = srcWriterFor(srcFolder());
		writer.write("bricks.y.Y", "@" + Brick.class.getName() + " public interface Y {}");
		writer.write("bricks.y.impl.YImpl", "class YImpl implements bricks.y.Y {}");
		
		my(Logger.class).log("Starting discovery of local bricks...");
		my(BrickSpace.class);
		
		waitForAvailableBrick("bricks.y.Y");
		
		BrickInfo Y = single(my(BrickSpace.class).availableBricks());
		Y.setStagedForInstallation(single(Y.versions()), true);
	}

	
	private File javaFileNameAt(File rootFolder, Class<?> clazz) {
		return new File(rootFolder, classUtils().relativeJavaFileName(clazz));
	}
	
	
	private ClassUtils classUtils() {
		return my(ClassUtils.class);
	}
	
	
	private File srcFolder() {
		return new File(tmpFolder(), "platform-src");
	}

	private void assertStagedFilesExist(String... fileNames) {
		for (String fileName : fileNames) assertStagedFileExists(fileName);
	}


	private void assertStagedFileExists(String fileName) {
		assertExists(stagedFile(fileName));
	}
	
	
	private File stagedFile(String fileName) {
		return new File(stagingFolder(), fileName);
	}


	private File stagingFolder() {
		return my(FolderConfig.class).tmpFolderFor(BrickInstaller.class);
	}

	
	private void copyClassToSrcFolder(final Class<?> clazz) throws IOException {
		my(IO.class).files().copyFile(
			platformSrcFileFor(clazz),
			srcFileFor(clazz));
	}

	
	private File srcFileFor(final Class<?> clazz) {
		return javaFileNameAt(srcFolder(), clazz);
	}

	
	private File platformSrcFileFor(final Class<?> clazz) {
		return new File(platformSrcFolder(), classUtils().relativeJavaFileName(clazz));
	}
	
	
	private File platformSrcFolder() {
		return new File(platformBin(), "src");
	}

	
	private File platformBin() {
		return classUtils().classpathRootFor(Brick.class).getParentFile();
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
		for (BrickInfo brickInfo : my(BrickSpace.class).availableBricks())
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
