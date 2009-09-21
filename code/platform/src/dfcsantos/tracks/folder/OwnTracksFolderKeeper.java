package dfcsantos.tracks.folder;

import java.io.File;

import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick
public interface OwnTracksFolderKeeper {

		Signal<File> ownTracksFolder();
		void setOwnTracksFolder(File ownTracksFolder);
}
