package dfcsantos.tracks;

import java.io.File;
import java.util.List;

import basis.brickness.Brick;


@Brick
public interface Tracks {

	List<File> listMp3FilesFromFolder(File folder);

	List<Track> listTracksFromFolder(File folder);

	Track newTrack(File trackFile);

}
