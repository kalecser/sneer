package sneer.bricks.softwaresharing.installer.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sneer.bricks.hardware.clock.Clock;
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
	public void commitStagedBricksInstallation() throws IOException {
		if (!_srcStage.exists()) return;

		File backupFolder = createBackupFolder();
		backup(platformSrc(), backupFolder, "src");
		backup(platformBin(), backupFolder, "bin");
		
		my(IO.class).files().copyFolder(_srcStage, platformSrc());
		my(IO.class).files().copyFolder(_binStage, platformBin());

		clean(_srcStage);
		clean(_binStage);
	}

	private void backup(File folder, File backupFolder, String backupName) throws IOException {
		File destFolder = new File(backupFolder, backupName);
		my(IO.class).files().copyFolder(folder, destFolder);
	}

	private File createBackupFolder() {
		File tmpFolder = my(FolderConfig.class).tmpFolderFor(BrickInstaller.class);
		return new File(tmpFolder, "backup/" + my(Clock.class).time());
	}

	@Override
	public void prepareStagedBricksInstallation() throws IOException, JavaCompilerException {
		prepareFolder(_srcStage);
		prepareFolder(_binStage);

		prepareStagedSrc();
		prepareStagedBin();
	}

	private void prepareStagedBin() throws JavaCompilerException, IOException {
		my(JavaCompiler.class).compile(_srcStage, _binStage, platformBin());
	}
	
	private File platformBin() {
		return my(FolderConfig.class).platformBinFolder().get();
	}
	
	private File platformSrc() {
		return my(FolderConfig.class).platformSrcFolder().get();
	}
	
	private void prepareStagedSrc() throws IOException {
		List<String> stagedBrickNames = new ArrayList<String>();
		
		for(BrickInfo brickInfo: my(BrickSpace.class).availableBricks())
			for (BrickVersion version : brickInfo.versions())
				if (version.isStagedForExecution()) {
					prepareStagedSrc(brickSrcFolder(brickInfo), version);
					stagedBrickNames.add(brickInfo.name());
					break;
				}
		
		writeBrickListFile(stagedBrickNames);
	}

	private void writeBrickListFile(List<String> stagedBrickNames)
			throws IOException {
		my(IO.class).files().writeString(brickListFile(), my(Lang.class).strings().join(stagedBrickNames, "\n"));
	}

	private File brickListFile() {
		return new File(_srcStage, "bricks.lst");
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

	private void prepareFolder(File folder) throws IOException {
		clean(folder);
		if (!folder.mkdirs())
			throw new IOException("Unable to create folder: " + folder);
	}

	private void clean(File folder) throws IOException {
		my(IO.class).files().forceDelete(folder);
	}

	

}
