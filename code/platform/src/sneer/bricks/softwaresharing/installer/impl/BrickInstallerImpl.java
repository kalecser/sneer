package sneer.bricks.softwaresharing.installer.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.software.bricks.compiler.BrickCompiler;
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
		
		delete(platformSrc());
		delete(platformBin());
		
		my(IO.class).files().copyFolder(_srcStage, platformSrc());
		my(IO.class).files().copyFolder(_binStage, platformBin());
		
		delete(_srcStage);
		delete(_binStage);
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
	public void prepareStagedBricksInstallation() throws IOException {
		resetFolder(_srcStage);
		resetFolder(_binStage);
		
		prepareStagedSrc();
		prepareStagedBin();
	}

	private void prepareStagedBin() throws IOException {
		
		copyPlatformBinFolder("sneer/foundation");
		copyPlatformBinFolder("sneer/main");
		copyPlatformBinFolder("sneer/tests");
		
		my(BrickCompiler.class).compile(_srcStage, _binStage);
	}

	private void copyPlatformBinFolder(String folderName) throws IOException {
		copyFolder(
			new File(platformBin(), folderName),
			new File(_binStage, folderName));
	}
	
	private File platformBin() {
		return my(FolderConfig.class).platformBinFolder().get();
	}
	
	private File platformSrc() {
		return my(FolderConfig.class).platformSrcFolder().get();
	}
	
	private void prepareStagedSrc() throws IOException {
		copyFolder(platformSrc(), _srcStage);		
		
		for (BrickInfo brickInfo : stagedBricks())
			prepareStagedSrc(brickInfo);
	}

	private void copyFolder(File from, File to) throws IOException {
		my(IO.class).files().copyFolder(from, to);
	}

	private List<BrickInfo> stagedBricks() {
		List<BrickInfo> result = new ArrayList<BrickInfo>();
		for(BrickInfo brickInfo: my(BrickSpace.class).availableBricks()) {
			BrickVersion version = brickInfo.getVersionStagedForInstallation();
			if (version != null) result.add(brickInfo);
		}
		if (result.isEmpty()) throw new IllegalStateException("No staged brick were found.");
		
		return result;
	}

	private void prepareStagedSrc(BrickInfo brickInfo) throws IOException {
		prepareStagedSrc(brickSrcFolder(brickInfo), brickInfo.getVersionStagedForInstallation());
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
		delete(new File(brickFolder, "impl"));
		delete(new File(brickFolder, "tests"));
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
		srcFile.setLastModified(fileVersion.lastModified());
	}

	private void write(File file, byte[] bytes) throws IOException {
		my(IO.class).files().writeByteArrayToFile(file, bytes);
	}

	private void resetFolder(File folder) throws IOException {
		delete(folder);
		if (!folder.mkdirs())
			throw new IOException("Unable to create folder: " + folder);
	}

	private void delete(File folder) throws IOException {
		my(IO.class).files().forceDelete(folder);
	}

	

}
