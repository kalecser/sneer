package sneer.bricks.softwaresharing.installer.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.software.code.compilers.classpath.Classpath;
import sneer.bricks.software.code.compilers.classpath.ClasspathFactory;
import sneer.bricks.software.code.compilers.java.JavaCompiler;
import sneer.bricks.software.code.compilers.java.Result;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.softwaresharing.BrickInfo;
import sneer.bricks.softwaresharing.BrickSpace;
import sneer.bricks.softwaresharing.BrickVersion;
import sneer.bricks.softwaresharing.FileVersion;
import sneer.bricks.softwaresharing.installer.BrickCompilationException;
import sneer.bricks.softwaresharing.installer.BrickInstaller;

public class BrickInstallerImpl implements BrickInstaller {

	private final File _srcFolder  = new File(my(FolderConfig.class).tmpFolderFor(BrickInstaller.class), "src");
	private final File _binFolder  = new File(my(FolderConfig.class).tmpFolderFor(BrickInstaller.class), "bin");

	@Override
	public void commitStagedBricksInstallation() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public void prepareStagedBricksInstallation() throws IOException, BrickCompilationException {
		prepareStagedSrc();
		prepareStagedBin();
	}

	
	private void prepareStagedBin() throws BrickCompilationException, IOException {
		Classpath classpath = my(ClasspathFactory.class).sneerApi();
		List<File> srcFiles = new ArrayList<File>(my(IO.class).files().listFiles(_srcFolder, new String[]{"java"}, true));
		Result result = my(JavaCompiler.class).compile(srcFiles, _binFolder, classpath);
		if (!result.success()) throw new BrickCompilationException(result.getErrorString());
	}

	
	private void prepareStagedSrc() throws IOException {
		for(BrickInfo brickInfo: my(BrickSpace.class).availableBricks())
			for (BrickVersion version : brickInfo.versions())
				if (version.isStagedForExecution()) {
					prepareStagedSrc(brickSrcFolder(brickInfo), version);
					break;
				}
	}

	private File brickSrcFolder(BrickInfo brickInfo) {
		return new File(_srcFolder, packageFolder(brickInfo));
	}

	private String packageFolder(BrickInfo brickInfo) {
		return packageName(brickInfo).replace(".", "/");
	}

	private String packageName(BrickInfo brickInfo) {
		return my(Lang.class).strings().substringBeforeLast(brickInfo.name(), ".");
	}

	private void prepareStagedSrc(File brickSrcFolder, BrickVersion version) throws IOException {
		for (FileVersion fileVersion : version.files())
			prepareStagedSrc(brickSrcFolder, fileVersion);
	}

	private void prepareStagedSrc(File brickSrcFolder, FileVersion fileVersion) throws IOException {
		File srcFile = new File(brickSrcFolder, fileVersion.name());
		write(srcFile, fileVersion.contents());
	}

	private void write(File file, byte[] bytes) throws IOException {
		my(IO.class).files().writeByteArrayToFile(file, bytes);
	}

}
