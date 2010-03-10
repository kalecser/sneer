package sneer.bricks.software.folderconfig.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.hardware.ram.ref.immutable.ImmutableReference;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReferences;
import sneer.bricks.software.folderconfig.FolderConfig;


public class FolderConfigImpl implements FolderConfig {

	private final ImmutableReference<File> _ownSrcFolder = immutable();
	private final ImmutableReference<File> _ownBinFolder = immutable();

	private final ImmutableReference<File> _srcFolder = immutable();
	private final ImmutableReference<File> _binFolder = immutable();
	private final ImmutableReference<File> _stageFolder = immutable();

	private final ImmutableReference<File> _storageFolder = immutable();
	private final ImmutableReference<File> _tmpFolder = immutable();

	@Override
	public ImmutableReference<File> ownBinFolder() {
		return _ownBinFolder;
	}

	@Override
	public ImmutableReference<File> binFolder() {
		return _binFolder;
	}

	@Override
	public ImmutableReference<File> storageFolder() {
		return _storageFolder;
	}

	@Override
	public File storageFolderFor(Class<?> brick) {
		return brickFolderIn(storageFolder().get(), brick);
	}

	private File brickFolderIn(File parent, Class<?> brick) {
		final File folder = new File(parent, "bricks/" + brick.getSimpleName());
		folder.mkdirs();
		return folder;
	}

	private static <T> ImmutableReference<T> immutable() {
		return my(ImmutableReferences.class).newInstance();
	}

	@Override
	public ImmutableReference<File> ownSrcFolder() {
		return _ownSrcFolder;
	}

	@Override
	public ImmutableReference<File> srcFolder() {
		return _srcFolder;
	}

	@Override
	public File tmpFolderFor(Class<?> brick) {
		return brickFolderIn(tmpFolder().get(), brick);
	}

	@Override
	public ImmutableReference<File> tmpFolder() {
		return _tmpFolder;
	}

	@Override
	public ImmutableReference<File> stageFolder() {
		return _stageFolder;
	}
	
}
