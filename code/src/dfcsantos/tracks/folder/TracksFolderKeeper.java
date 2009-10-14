package dfcsantos.tracks.folder;

import java.io.File;

import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick
public interface TracksFolderKeeper {

	Signal<File> ownTracksFolder();
	void setOwnTracksFolder(File ownTracksFolder);

	Signal<File> sharedTracksFolder();
	void setSharedTracksFolder(File sharedTracksFolder);

}
