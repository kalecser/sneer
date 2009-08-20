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
		
		clean(platformSrc());
		clean(platformBin());
		
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
		
		List<BrickInfo> stagedBricks = stagedBricks();
		prepareStagedSrc(stagedBricks);
		prepareStagedBin(stagedBricks);
	}

	private void prepareStagedBin(List<BrickInfo> stagedBricks) throws JavaCompilerException, IOException {
		
//		my(IO.class).files().copyFolder(platformBin(), _binStage);
		for (BrickInfo brickInfo : stagedBricks)
			prepareStagedBin(brickInfo);
		
		my(JavaCompiler.class).compile(_srcStage, _binStage, platformBin());
	}
	
	private void prepareStagedBin(BrickInfo brickInfo) throws IOException {
		cleanStagedBrickFolder(new File(_binStage, packageFolder(brickInfo)));
	}

	private File platformBin() {
		return my(FolderConfig.class).platformBinFolder().get();
	}
	
	private File platformSrc() {
		return my(FolderConfig.class).platformSrcFolder().get();
	}
	
	private void prepareStagedSrc(List<BrickInfo> stagedBricks) throws IOException {
		
		my(IO.class).files().copyFolder(platformSrc(), _srcStage);		
		
		for (BrickInfo brickInfo : stagedBricks)
			prepareStagedSrc(brickInfo);
		
	}

	private List<BrickInfo> stagedBricks() {
		List<BrickInfo> stagedBricks = new ArrayList<BrickInfo>();
		for(BrickInfo brickInfo: my(BrickSpace.class).availableBricks()) {
			BrickVersion version = brickInfo.getVersionStagedForExecution();
			if (version != null) stagedBricks.add(brickInfo);
		}
		return stagedBricks;
	}

	private void prepareStagedSrc(BrickInfo brickInfo) throws IOException {
		prepareStagedSrc(brickSrcFolder(brickInfo), brickInfo.getVersionStagedForExecution());
	}

	private File brickSrcFolder(BrickInfo brickInfo) {
		return new File(_srcStage, packageFolder(brickInfo));
	}

	private String packageFolder(BrickInfo brickInfo) {
		return packageFolder(brickInfo.name());
	}

	private String packageFolder(String brickName) {
		return packageName(brickName).replace(".", "/");
	}

	private String packageName(String brickName) {
		return my(Lang.class).strings().substringBeforeLast(brickName, ".");
	}

	private void prepareStagedSrc(File brickSrcFolder, BrickVersion version) throws IOException {
		cleanStagedBrickFolder(brickSrcFolder);
		for (FileVersion fileVersion : version.files())
			prepareStagedSrc(brickSrcFolder, fileVersion);
	}

	private void cleanStagedBrickFolder(File brickFolder) throws IOException {
		deleteFilesIn(brickFolder);
		clean(new File(brickFolder, "impl"));
		clean(new File(brickFolder, "tests"));
	}

	private void deleteFilesIn(File brickFolder) {
		File[] files = brickFolder.listFiles();
		if (files == null) return;
		
		for (File f : files)
			if (f.isFile()) f.delete();
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
