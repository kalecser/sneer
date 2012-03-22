package dfcsantos.tracks.storage.folder;

import java.io.File;

import basis.brickness.Brick;

import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.pulp.reactive.Signal;

@Brick(Prevalent.class)
public interface TracksFolderKeeper {

	Signal<File> playingFolder();
	void setPlayingFolder(String playingFolder);

	Signal<File> tracksFolder();
	void setTracksFolder(String sharedTracksFolder);

	File inboxFolder();
	File noveltiesFolder();

}
