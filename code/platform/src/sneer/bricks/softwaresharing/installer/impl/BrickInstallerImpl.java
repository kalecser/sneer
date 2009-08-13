package sneer.bricks.softwaresharing.installer.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.software.code.compilers.java.JavaCompiler;
import sneer.bricks.software.code.compilers.java.JavaCompilerException;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.softwaresharing.BrickInfo;
import sneer.bricks.softwaresharing.BrickSpace;
import sneer.bricks.softwaresharing.BrickVersion;
import sneer.bricks.softwaresharing.FileVersion;
import sneer.bricks.softwaresharing.installer.BrickInstaller;

public class BrickInstallerImpl implements BrickInstaller {

	private final File _srcStage  = new File(my(FolderConfig.class).tmpFolderFor(BrickInstaller.class), "src");
	private final File _binStage  = new File(my(FolderConfig.class).tmpFolderFor(BrickInstaller.class), "bin");

	@Override
	public void commitStagedBricksInstallation() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public void prepareStagedBricksInstallation() throws IOException, JavaCompilerException {
		prepareStagedSrc();
		prepareStagedBin();
	}

	
	private void prepareStagedBin() throws JavaCompilerException, IOException {
		my(JavaCompiler.class).compile(_srcStage, _binStage, sneerApi());
	}

	
	private File sneerApi() {
		return my(FolderConfig.class).platformBinFolder().get();
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
		return new File(_srcStage, packageFolder(brickInfo));
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
