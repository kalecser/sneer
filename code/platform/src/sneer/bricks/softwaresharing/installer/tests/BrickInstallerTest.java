package sneer.bricks.softwaresharing.installer.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.Arrays;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.pulp.reactive.collections.SetRegister;
import sneer.bricks.pulp.reactive.collections.impl.SetRegisterImpl;
import sneer.bricks.software.bricks.snappstarter.Snapp;
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
	
	@Test
	public void prepareStagedBricksInstallation() throws Exception  {
		
		checking(new Expectations() {{
			
			final BrickInfo brick = mock(BrickInfo.class);
			allowing(_brickSpace).availableBricks();
				will(returnValue(newSetRegister(brick).output()));
			
			allowing(brick).name(); will(returnValue("bricks.y.Y"));
				
			final BrickVersion version1 = mock("version1", BrickVersion.class);
			final BrickVersion version2 = mock("version2", BrickVersion.class);
			allowing(brick).versions();
				will(returnValue(Arrays.asList(version1, version2)));
				
			allowing(version1).isStagedForExecution();
				will(returnValue(true));
				
			allowing(version2).isStagedForExecution();
				will(returnValue(false));

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
				
		}});
		
		my(FolderConfig.class).platformBinFolder().set(binFolder());
		
		_subject.prepareStagedBricksInstallation();
		
		assertStagedFilesExist(
			"src/bricks/y/Y.java",
			"src/bricks/y/impl/YImpl.java",
			"bin/bricks/y/Y.class",
			"bin/bricks/y/impl/YImpl.class"
		);
		
	}


	private File binFolder() {
		return my(ClassUtils.class).classpathRootFor(BrickInstaller.class);
	}


	private void assertStagedFilesExist(String... fileNames) {
		for (String fileName : fileNames) assertStagedFileExists(fileName);
	}


	private void assertStagedFileExists(String fileName) {
		assertExists(new File(installerFolder(), fileName));
	}


	private File installerFolder() {
		return my(FolderConfig.class).tmpFolderFor(BrickInstaller.class);
	}


	private String interfaceDefinition() {
		return "package bricks.y;\n" +
		"@" + Brick.class.getName() + " " +
		"@" + Snapp.class.getName() + " " +
		"public interface Y {}";
	}

	private String implDefinition() {
		return "package bricks.y.impl;\n" +
		"class YImpl implements bricks.y.Y {}";
	}


	private <T> SetRegister<T> newSetRegister(T... elements) {
		final SetRegister<T> result = new SetRegisterImpl<T>();
		result.addAll(Arrays.asList(elements));
		return result;
	}

}
