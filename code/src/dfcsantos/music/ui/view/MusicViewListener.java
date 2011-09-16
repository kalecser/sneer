package dfcsantos.music.ui.view;

import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.collections.ListSignal;

public interface MusicViewListener {
	
	Register<Boolean> isTrackExchangeActive();
	Register<Integer> volumePercent();
	Register<Boolean> shuffle();

	Signal<String> playingTrackName();
	Signal<Integer> playingTrackTime();
	ListSignal<String> playingFolderChoices();
	
	void chooseTracksFolder();
	void pauseResume();
	void skip();
	void stop();
	void deleteTrack();
	void meToo();
	void noWay();
	void playingFolderChosen(String folder);
}