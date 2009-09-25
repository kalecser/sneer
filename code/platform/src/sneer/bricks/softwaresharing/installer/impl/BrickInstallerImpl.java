package sneer.bricks.softwaresharing.installer.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardwaresharing.files.writer.FileWriter;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.software.bricks.compiler.BrickCompilerException;
import sneer.bricks.software.bricks.compiler.Builder;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.softwaresharing.BrickInfo;
import sneer.bricks.softwaresharing.BrickSpace;
import sneer.bricks.softwaresharing.BrickVersion;
import sneer.bricks.softwaresharing.installer.BrickInstaller;

public class BrickInstallerImpl implements BrickInstaller {

	private final File _srcStage  = new File(my(FolderConfig.class).tmpFolderFor(BrickInstaller.class), "src");
	private final File _binStage  = new File(my(FolderConfig.class).tmpFolderFor(BrickInstaller.class), "bin");


	@Override
	public void stageBricksForInstallation() {
		try {
			resetFolder(_srcStage);
			resetFolder(_binStage);
			
			prepareStagedSrc();
			prepareStagedBin();
		} catch (Exception e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Brick Installation Error", "Call your sneer buddy", e);
		}
	}

	
	private void prepareStagedBin() throws IOException, BrickCompilerException {
		my(Builder.class).build(_srcStage, _binStage);
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
		if (brickSrcFolder.exists())
			cleanStagedBrickFolder(brickSrcFolder);
		else
			brickSrcFolder.mkdirs();
		
		my(FileWriter.class).mergeOver(brickSrcFolder, version.hash());
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

	
	private void resetFolder(File folder) throws IOException {
		delete(folder);
		if (!folder.mkdirs())
			throw new IOException("Unable to create folder: " + folder);
	}

	private void delete(File folder) throws IOException {
		my(Logger.class).log("Deleting: ", folder);
		my(IO.class).files().forceDelete(folder);
	}

	

}
