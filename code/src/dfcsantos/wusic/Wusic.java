package dfcsantos.wusic;

import java.io.File;

import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.software.bricks.snappstarter.Snapp;
import sneer.foundation.brickness.Brick;

@Snapp
@Brick
public interface Wusic {

	void setMyTracksFolder(File selectedFolder);

	enum OperatingMode { SOLO, PEERS };
	void setOperatingMode(OperatingMode mode);

	void start();
	Signal<String> playingTrackName();
	Signal<String> playingTrackTime();

	void pauseResume();
	void stop();
	void skip();

	void meToo();
	void noWay();

	void setShuffleMode(boolean shuffle);

}
