package dfcsantos.tracks.storage.folder;

import java.io.File;

import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick(Prevalent.class)
public interface TracksFolderKeeper {

	Signal<File> playingFolder();
	void setPlayingFolder(File playingFolder);

	Signal<File> tracksFolder();
	void setTracksFolder(File sharedTracksFolder);

	File inboxFolder();
	File noveltiesFolder();

}
