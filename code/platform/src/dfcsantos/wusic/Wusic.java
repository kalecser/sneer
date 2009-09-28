package dfcsantos.wusic;

import java.io.File;

import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.software.bricks.snappstarter.Snapp;
import sneer.foundation.brickness.Brick;

@Snapp
@Brick
public interface Wusic {

	void setMyTracksFolder(File selectedFolder);

	public enum TrackSource { OWN_TRACKS, PEER_TRACKS_STAGING_AREA }
	void chooseTrackSource(TrackSource source);

	void start();
	Signal<String> trackPlayingName();

	void pauseResume();
	void stop();
	void skip();

	void meToo();
	void noWay();

	void setShuffleMode(boolean shuffle);

}
