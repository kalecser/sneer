package dfcsantos.tracks.folder.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.FolderConfig;
import dfcsantos.tracks.folder.TracksFolderKeeper;

class TracksFolderKeeperImpl implements TracksFolderKeeper {

	private final Register<File> _playingFolder = my(Signals.class).newRegister(defaultTracksFolder());
	private final Register<File> _sharedTracksFolder = my(Signals.class).newRegister(defaultTracksFolder());

	private File _peerTracksFolder;

	private File defaultTracksFolder() {
		return mkDirs(new File(my(FolderConfig.class).storageFolder().get() ,"media/tracks"));
	}

	@Override
	public Signal<File> playingFolder() {
		return _playingFolder.output();
	}

	@Override
	public void setPlayingFolder(File playingFolder) {
		_playingFolder.setter().consume(playingFolder);
	}


	@Override
	public Signal<File> sharedTracksFolder() {
		return _sharedTracksFolder.output();
	}

	@Override
	public void setSharedTracksFolder(File sharedTracksFolder) {
		_sharedTracksFolder.setter().consume(sharedTracksFolder);
	}

	@Override
	public File peerTracksFolder() {
		if (_peerTracksFolder == null)
			_peerTracksFolder = mkDirs(new File(my(FolderConfig.class).tmpFolderFor(TracksFolderKeeper.class), "peertracks"));
				
		return _peerTracksFolder;
	}

	
	private File mkDirs(File folder) {
		if (!folder.exists() && !folder.mkdirs())
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Unable to create folder.", "Unable to create folder: " + folder);
		return folder;
	}


}
