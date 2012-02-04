package dfcsantos.music.ui.view;

import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.collections.ListSignal;

public interface MusicViewListener {
	
	Register<Boolean> isTrackExchangeActive();
	Register<Integer> volumePercent();
	Register<Boolean> shuffle();

	Signal<Boolean> isPlaying();
	Signal<String> playingTrackName();
	Signal<Integer> playingTrackTime();
	ListSignal<String> playingFolderChoices();
	String playingFolder();

	void playingFolderChosen(String folder);
	void chooseTracksFolder();
	void pauseResume();
	void skip();
	void stop();

	void meToo();
	void meh();
	void noWay();

	Signal<Boolean> enableMeToo();
	Signal<Boolean> enableTrackDownloaded();
}