package sneer.bricks.software.folderconfig;

import java.io.File;

import sneer.bricks.hardware.ram.ref.immutable.ImmutableReference;
import sneer.foundation.brickness.Brick;

@Brick
public interface FolderConfig {

	ImmutableReference<File> ownSrcFolder();
	ImmutableReference<File> ownBinFolder();

	ImmutableReference<File> srcFolder();
	ImmutableReference<File> binFolder();

	ImmutableReference<File> storageFolder();
	File storageFolderFor(Class<?> brick);

	ImmutableReference<File> tmpFolder();
	File tmpFolderFor(Class<?> brick);

	ImmutableReference<File> stageFolder();

}
