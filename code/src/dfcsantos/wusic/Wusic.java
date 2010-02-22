package dfcsantos.wusic;

import java.io.File;

import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.PickyConsumer;
import dfcsantos.tracks.Track;

@Brick
public interface Wusic {

	enum OperatingMode { OWN, PEERS };
	void switchOperatingMode();
	Signal<OperatingMode> operatingMode();

	void setPlayingFolder(File selectedFolder);
	void setSharedTracksFolder(File selectedFolder);

	void setShuffle(boolean shuffle);

	void start();
	void pauseResume();
	void back();
	void skip();
	void stop();

	void meToo();
	void deleteTrack();

	Signal<Boolean> isPlaying();
	Signal<Track>	playingTrack();
	Signal<Integer> playingTrackTime();

	Signal<Integer> numberOfPeerTracks();

	Signal<Boolean> isTracksDownloadActive();
	Consumer<Boolean> tracksDownloadActivator();

	int DEFAULT_TRACKS_DOWNLOAD_ALLOWANCE = 100; // MBs
	Signal<Integer> tracksDownloadAllowance();
	PickyConsumer<Integer> tracksDownloadAllowanceSetter();

}

