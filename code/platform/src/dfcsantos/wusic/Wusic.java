package dfcsantos.wusic;

import java.io.File;

import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.software.bricks.snappstarter.Snapp;
import sneer.foundation.brickness.Brick;

@Snapp
@Brick
public interface Wusic {

	public enum TrackSource { MY_TRACKS, PEER_TRACKS_STAGING_AREA }
	
	void start();
	
	void setMyTracksFolder(File selectedFolder);
	void chooseTrackSource(TrackSource source);
	
	Signal<String> trackPlaying();
	EventSource<Track> trackPlayed();

	void pauseResume();
	void skip();
	
	void meToo();
	void noWay();


}
