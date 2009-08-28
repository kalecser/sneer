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
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.code.java.source.writer.JavaSourceWriter;
import sneer.bricks.software.code.java.source.writer.JavaSourceWriters;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.softwaresharing.BrickInfo;
import sneer.bricks.softwaresharing.BrickSpace;
import sneer.bricks.softwaresharing.installer.BrickInstaller;
import sneer.foundation.brickness.Brick;
import sneer.foundation.brickness.Seal;
import sneer.foundation.brickness.testsupport.BrickTest;
import sneer.foundation.lang.Consumer;

public class BrickInstallerTest extends BrickTest {
	
	final BrickInstaller _subject = my(BrickInstaller.class);
	
	@Before
	public void beforeBrickInstallerTest() throws IOException {
		setUpPlatform();
		stageBrickY();
	}

	
	private void setUpPlatform() throws IOException {
		binFolder().mkdirs();
		srcFolder().mkdirs();
		
		my(FolderConfig.class).platformBinFolder().set(binFolder());
		my(FolderConfig.class).platformSrcFolder().set(srcFolder());
		
		copyClassToBinFolder(sneer.foundation.brickness.Brick.class);
		copyClassToBinFolder(sneer.main.Sneer.class);
		copyClassToBinFolder(sneer.tests.freedom1.Freedom1TestBase.class);
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


	@Test (timeout = 4000)
	public void stageOneBrick() throws Exception  {
		_subject.prepareStagedBricksInstallation();
		
		assertStagedFilesExist(
			"bin/sneer/foundation/brickness/Brick.class",
			"bin/sneer/main/Sneer.class",
			"bin/sneer/tests/freedom1/Freedom1TestBase.class",

			"src/bricks/y/Y.java",
			"src/bricks/y/impl/YImpl.java",
			"bin/bricks/y/Y.class",
			"bin/bricks/y/impl/YImpl.class"
		);
		
	}


	@Test
	public void commitStagedFiles() throws Exception {
		
		File nestedBrickSrc = createFile(srcFolder(), "bricks/y/nested/NestedBrick.java",
			"package bricks.y.nested;" +
			"public interface NestedBrick {}");
		File nestedBrickBin = createFile(binFolder(), "bricks/y/nested/NestedBrick.class");

		_subject.prepareStagedBricksInstallation();

		File garbageSrc      = createFile(srcFolder(), "bricks/y/Garbage.java");
		File garbageImplSrc  = createFile(srcFolder(), "bricks/y/impl/GarbageImpl.java");
		File garbageTestsSrc = createFile(srcFolder(), "bricks/y/tests/GarbageTest.java");
		File garbageBin      = createFile(binFolder(), "bricks/y/Garbage.class");
		File garbageImplBin  = createFile(binFolder(), "bricks/y/impl/GarbageImpl.class");
		File garbageTestsBin = createFile(binFolder(), "bricks/y/tests/GarbageTest.class");
		File sneerGarbage    = createFile(binFolder(), "sneer/garbage/Garbage.class");
		
		my(Logger.class).log("Comitting...");
		_subject.commitStagedBricksInstallation();
		
		assertExists(
				nestedBrickSrc,
				nestedBrickBin,
				
				binFileFor(sneer.foundation.brickness.Brick.class),
				binFileFor(sneer.main.Sneer.class),
				binFileFor(sneer.tests.freedom1.Freedom1TestBase.class));
		
		assertDoesNotExist(
				garbageSrc,
				garbageImplSrc,
				garbageTestsSrc,
				garbageBin,
				garbageImplBin,
				garbageTestsBin,
				sneerGarbage);
	}

	
	private File binFileFor(Class<?> clazz) {
		return new File(binFolder(), my(ClassUtils.class).relativeClassFileName(clazz));
	}

	
	private File createFile(File folder, String filename, String data) throws IOException {
		File file = createFile(folder, filename);
		my(IO.class).files().writeString(file, data);
		return file;
	}

	
	private File createFile(File parent, String filename) throws IOException {
		final File file = new File(parent, filename);
		file.getParentFile().mkdirs();
		file.createNewFile();
		return file;
	}

	
	private File srcFolder() {
		return new File(tmpFolder(), "platform-src");
	}


	private File binFolder() {
		return new File(tmpFolder(), "platform-bin");
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

	
	private void copyClassToBinFolder(final Class<?> clazz) throws IOException {
		my(IO.class).files().copyFile(
			my(ClassUtils.class).classFile(clazz),
			new File(binFolder(), clazz.getName().replace('.', '/') + ".class"));
	}

	
	private <T> T single(Collection<T> collection) {
		assertEquals(1, collection.size());
		return collection.iterator().next();
	}


	private void waitForAvailableBrick(final String brickName) {
		final Latch latch = my(Latches.class).newLatch();
		
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
	



}
