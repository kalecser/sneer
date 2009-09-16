package dfcsantos.wusic;

import java.io.File;

import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick
public interface Wusic {

	public enum SongSource { MY_SONGS, PEER_SONGS_STAGING_AREA }
	
	void start();
	
	void setMySongsFolder(File selectedFolder);
	void chooseSongSource(SongSource source);
	
	Signal<String> songPlaying();

	void pauseResume();
	void skip();
	
	void meToo();
	void noWay();

}
