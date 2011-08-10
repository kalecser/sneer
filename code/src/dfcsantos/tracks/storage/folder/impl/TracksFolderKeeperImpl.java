package dfcsantos.tracks.storage.folder.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.FolderConfig;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;

class TracksFolderKeeperImpl implements TracksFolderKeeper {

	private final Register<File> tracksFolder = my(Signals.class).newRegister(null);
	private final Register<File> playingFolder = my(Signals.class).newRegister(null);
	private transient File inboxFolder;

	
	@Override public Signal<File> tracksFolder() { return tracksFolder.output(); }
	@Override public void setTracksFolder(File sharedTracksFolder) { tracksFolder.setter().consume(sharedTracksFolder); }
	
	
	@Override public Signal<File> playingFolder() { return playingFolder.output(); }
	@Override public void setPlayingFolder(File playingFolder) { this.playingFolder.setter().consume(playingFolder); }


	@Override
	public File noveltiesFolder() {
		File tf = tracksFolder().currentValue();
		return tf == null
			? null
			: new File(tf, "novelties");
	}

	
	@Override
	public File inboxFolder() {
		if (inboxFolder == null)
			inboxFolder = initInboxFolder();
				
		return inboxFolder;
	}
	
	
	private File initInboxFolder() {
		return mkDirs(new File(tmpFolder(), "peertracks"));
	}
	
	
	private File tmpFolder() {
		return my(FolderConfig.class).tmpFolderFor(TracksFolderKeeper.class);
	}

	
	private File mkDirs(File folder) {
		if (!folder.exists() && !folder.mkdirs())
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Unable to create folder.", "Unable to create folder: " + folder);
		return folder;
	}
	
}
