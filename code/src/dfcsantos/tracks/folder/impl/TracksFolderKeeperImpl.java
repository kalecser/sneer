package dfcsantos.tracks.folder.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.FolderConfig;
import dfcsantos.tracks.folder.TracksFolderKeeper;

class TracksFolderKeeperImpl implements TracksFolderKeeper {

	private final Register<File> _ownTracksFolder = my(Signals.class).newRegister(defaultOwnTracksFolder());
	private final Register<File> _sharedTracksFolder = my(Signals.class).newRegister(defaultSharedTracksFolder());

	private File defaultOwnTracksFolder() {
		File result = new File(my(FolderConfig.class).storageFolder().get() ,"media/own_tracks");
		result.mkdirs();
		return result;
	}

	@Override
	public Signal<File> ownTracksFolder() {
		return _ownTracksFolder.output();
	}

	@Override
	public void setOwnTracksFolder(File ownTracksFolder) {
		_ownTracksFolder.setter().consume(ownTracksFolder);
	}

	private File defaultSharedTracksFolder() {
		File result = new File(my(FolderConfig.class).storageFolder().get() ,"media/shared_tracks");
		result.mkdirs();
		return result;
	}

	@Override
	public Signal<File> sharedTracksFolder() {
		return _sharedTracksFolder.output();
	}

	@Override
	public void setSharedTracksFolder(File ownTracksFolder) {
		_sharedTracksFolder.setter().consume(ownTracksFolder);
	}

}
