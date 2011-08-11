package dfcsantos.music.ui.view;

import sneer.bricks.pulp.reactive.Register;

public interface MusicViewListener {
	
	Register<Boolean> isTrackExchangeActive();
	Register<Integer> volumePercent();
	Register<Boolean> shuffle();

	void chooseTracksFolder();
	void pauseResume();
	void skip();
	void stop();
}