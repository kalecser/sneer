package sneer.bricks.softwaresharing.installer.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.softwaresharing.BrickInfo;
import sneer.bricks.softwaresharing.BrickSpace;
import sneer.bricks.softwaresharing.BrickVersion;
import sneer.bricks.softwaresharing.FileVersion;
import sneer.bricks.softwaresharing.installer.BrickInstaller;
import sneer.foundation.brickness.Brick;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.brickness.testsupport.BrickTest;

public class BrickInstallerTest extends BrickTest {
	
	@Bind final BrickSpace _brickSpace = mock(BrickSpace.class);
	
	final BrickInstaller _subject = my(BrickInstaller.class);
	
	@Before
	public void setUpPlatformBin() throws IOException {
		binFolder().mkdirs();
		srcFolder().mkdirs();

		my(FolderConfig.class).platformBinFolder().set(binFolder());
		my(FolderConfig.class).platformSrcFolder().set(srcFolder());

		copyClassToBinFolder(sneer.foundation.brickness.Brick.class);
		copyClassToBinFolder(sneer.main.Sneer.class);
		copyClassToBinFolder(sneer.tests.freedom1.Freedom1TestBase.class);
	}
	
	@Test
	public void prepareStagedBricksInstallation() throws Exception  {
		
		checking(new Expectations() {{
			
			final BrickInfo brick = mock(BrickInfo.class);
			allowing(_brickSpace).availableBricks();
				will(returnValue(Arrays.asList(brick)));
			
			allowing(brick).name(); will(returnValue("bricks.y.Y"));
			
			final BrickVersion version1 = mock("version1", BrickVersion.class);
			allowing(version1).isStagedForExecution();
				will(returnValue(true));
				
			allowing(brick).getVersionStagedForInstallation();
				will(returnValue(version1));
				
			FileVersion interfaceFile = mock("Y.java", FileVersion.class);
			FileVersion implFile = mock("YImpl.java", FileVersion.class);
			
			allowing(version1).files();
				will(returnValue(Arrays.asList(
						interfaceFile,
						implFile)));
				
			allowing(interfaceFile).name();	will(returnValue("Y.java"));
			allowing(implFile).name(); will(returnValue("impl/YImpl.java"));

			allowing(interfaceFile).contents();	will(returnValue(interfaceDefinition().getBytes("UTF-8")));
			allowing(implFile).contents(); will(returnValue(implDefinition().getBytes("UTF-8")));
				
			allowing(interfaceFile).lastModified();	will(returnValue(1L));
			allowing(implFile).lastModified(); will(returnValue(1L));
		}});
		
		_subject.prepareStagedBricksInstallation();
		
		assertStagedFilesExist(
			"src/bricks/y/Y.java",
			"src/bricks/y/impl/YImpl.java",
			"bin/bricks/y/Y.class",
			"bin/bricks/y/impl/YImpl.class"
		);
		
	}
	
	@Test
	public void commitStagedFiles() throws Exception {
		
		final File garbageSrc = createFile(srcFolder(), "bricks/y/Garbage.java");
		final File garbageImplSrc = createFile(srcFolder(), "bricks/y/impl/GarbageImpl.java");
		final File garbageTestsSrc = createFile(srcFolder(), "bricks/y/tests/GarbageTest.java");
		final File garbageBin = createFile(binFolder(), "bricks/y/Garbage.class");
		final File sneerGarbage = createFile(binFolder(), "sneer/garbage/Garbage.class");
		
		final File nestedBrickSrc = createFile(srcFolder(), "bricks/y/nested/NestedBrick.java",
				"package bricks.y.nested;" +
				"public interface NestedBrick {}");
		final File nestedBrickBin = createFile(binFolder(), "bricks/y/nested/NestedBrick.class");
		
		prepareStagedBricksInstallation();
		_subject.commitStagedBricksInstallation();
		
		assertExists(
				nestedBrickSrc,
				nestedBrickBin,
				
				binFileFor(Brick.class),
				binFileFor(sneer.main.Sneer.class),
				binFileFor(sneer.tests.freedom1.Freedom1TestBase.class));
		
		assertDoesNotExist(
				garbageSrc,
				garbageImplSrc,
				garbageTestsSrc,
				garbageBin,
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
		return new File(installerFolder(), fileName);
	}


	private File installerFolder() {
		return my(FolderConfig.class).tmpFolderFor(BrickInstaller.class);
	}


	private String interfaceDefinition() {
		return "package bricks.y;\n" +
		"@" + Brick.class.getName() + " " +
		"public interface Y {}";
	}

	private String implDefinition() {
		return "package bricks.y.impl;\n" +
		"class YImpl implements bricks.y.Y {}";
	}

	private void copyClassToBinFolder(final Class<?> clazz) throws IOException {
		my(IO.class).files().copyFile(
			my(ClassUtils.class).classFile(clazz),
			new File(binFolder(), clazz.getName().replace('.', File.separatorChar) + ".class"));
	}

}
