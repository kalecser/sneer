package dfcsantos.wusic;

import java.io.File;

import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.software.bricks.snappstarter.Snapp;
import sneer.foundation.brickness.Brick;

@Snapp
@Brick
public interface Wusic {

	enum OperatingMode { OWN, PEERS };
	void setOperatingMode(OperatingMode mode);
	Signal<OperatingMode> operatingMode();

	void setOwnTracksFolder(File selectedFolder);
	void setPeerTracksFolder(File selectedFolder);

	void setShuffleMode(boolean shuffle);

	void start();
	Signal<String> playingTrackName();
	Signal<String> playingTrackTime();

	void pauseResume();
	void skip();
	void stop();

	void meToo();
	void noWay();

}
