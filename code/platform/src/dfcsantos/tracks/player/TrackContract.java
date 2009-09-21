package dfcsantos.tracks.player;

import sneer.bricks.hardware.cpu.lang.contracts.Contract;


public interface TrackContract extends Contract {
	enum Status {NEW, PLAYING, PAUSED, DISPOSED};

	void startPlaying();
	void pauseResume();
	Status status();
}
