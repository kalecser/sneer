package dfcsantos.tracks.folder.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.FolderConfig;
import dfcsantos.tracks.folder.OwnTracksFolderKeeper;

class OwnTracksFolderKeeperImpl implements OwnTracksFolderKeeper {

	private final Register<File> _ownTracksFolder = my(Signals.class).newRegister(defaultTracksFolder());

	@Override
	public Signal<File> ownTracksFolder() {
		return _ownTracksFolder .output();
	}

	private File defaultTracksFolder() {
		File result = new File(my(FolderConfig.class).storageFolder().get() ,"media/tracks");
		result.mkdirs();
		return result;
	}

	@Override
	public void setOwnTracksFolder(File ownTracksFolder) {
		_ownTracksFolder.setter().consume(ownTracksFolder);
	}

}
