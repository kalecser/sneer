package dfcsantos.music.ui.view;

import java.util.Set;

import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;

public interface MusicViewListener {
	
	Register<Boolean> isTrackExchangeActive();
	Register<Integer> volumePercent();
	Register<Boolean> shuffle();
	
	Signal<String> playingTrackName();
	Signal<Integer> playingTrackTime();
	Signal<Set<String>> subSharedTracksFolders();
	
	void chooseTracksFolder();
	void pauseResume();
	void skip();
	void stop();
	void deleteTrack();
	void meToo();
	void noWay();
}