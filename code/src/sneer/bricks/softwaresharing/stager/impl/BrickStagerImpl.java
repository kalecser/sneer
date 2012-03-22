package sneer.bricks.softwaresharing.stager.impl;

import static basis.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.expression.files.writer.folder.FolderContentsWriter;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.software.bricks.compiler.BrickCompilerException;
import sneer.bricks.software.bricks.compiler.Builder;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.softwaresharing.BrickHistory;
import sneer.bricks.softwaresharing.BrickSpace;
import sneer.bricks.softwaresharing.BrickVersion;
import sneer.bricks.softwaresharing.stager.BrickStager;

public class BrickStagerImpl implements BrickStager {

	private final File _srcStage = staged("src");
	private final File _binStage = staged("bin");


	@Override
	public void stageBricksForInstallation() {
		try {
			resetFolder(stage());
			
			prepareStagedSrc();
			prepareStagedBin();
		} catch (Exception e) {
			deleteStage();
			turnOnBlinkingLightError(e);
		}
	}


	private void turnOnBlinkingLightError(Exception e) {
		my(BlinkingLights.class).turnOn(LightType.ERROR, "Brick Installation Error", "Call your sneer buddy", e);
	}
	
	private void deleteStage() {
		try {
			delete(stage());
		} catch (Exception e) {
			turnOnBlinkingLightError(e);
		}
	}

	private File staged(String folder) {
		return new File(stage(), folder);
	}


	private File stage() {
		return my(FolderConfig.class).stageFolder().get();
	}


	private void prepareStagedBin() throws IOException, BrickCompilerException {
		_binStage.mkdir();
		my(Builder.class).build(_srcStage, _binStage);
	}
	

	private File srcFolder() {
		return my(FolderConfig.class).srcFolder().get();
	}

	
	private void prepareStagedSrc() throws IOException {
		copyFolder(srcFolder(), _srcStage);
		
		for (BrickHistory brick : stagedBricks())
			prepareStagedSrc(brick);
	}


	private void copyFolder(File from, File to) throws IOException {
		my(IO.class).files().copyFolder(from, to);
	}


	private List<BrickHistory> stagedBricks() {
		List<BrickHistory> result = new ArrayList<BrickHistory>();
		for(BrickHistory brickInfo: my(BrickSpace.class).availableBricks()) {
			BrickVersion version = brickInfo.getVersionChosenForInstallation();
			if (version != null) result.add(brickInfo);
		}
		if (result.isEmpty()) throw new IllegalStateException("No staged brick were found.");
		
		return result;
	}


	private void prepareStagedSrc(BrickHistory brickHistory) throws IOException {
		prepareStagedSrc(brickSrcFolder(brickHistory), brickHistory.getVersionChosenForInstallation());
	}


	private File brickSrcFolder(BrickHistory brickInfo) {
		return new File(_srcStage, packageFolder(brickInfo));
	}


	private String packageFolder(BrickHistory brickInfo) {
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
		
		my(FolderContentsWriter.class).mergeOver(brickSrcFolder, folderContents(version));
	}


	private FolderContents folderContents(BrickVersion version) {
		return my(FileMap.class).getFolderContents(version.hash());
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
