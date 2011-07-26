package dfcsantos.music.ui.view;

import sneer.bricks.pulp.reactive.Signal;

public interface MusicViewListener {
	void chooseTracksFolder();
	void toggleTrackExchange();
	Signal<Boolean> isExchangingTracks();
}