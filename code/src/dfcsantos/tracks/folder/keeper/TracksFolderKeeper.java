package dfcsantos.tracks.folder.keeper;

import java.io.File;

import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick
public interface TracksFolderKeeper {

	Signal<File> playingFolder();
	void setPlayingFolder(File playingFolder);

	Signal<File> sharedTracksFolder();
	void setSharedTracksFolder(File sharedTracksFolder);

	File peerTracksFolder();

}
