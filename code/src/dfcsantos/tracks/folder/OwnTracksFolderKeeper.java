package dfcsantos.tracks.folder;

import java.io.File;

import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick
public interface OwnTracksFolderKeeper {

	void setOwnTracksFolder(File ownTracksFolder);
	Signal<File> ownTracksFolder();
	Signal<File> peerTracksFolder();
	
}
