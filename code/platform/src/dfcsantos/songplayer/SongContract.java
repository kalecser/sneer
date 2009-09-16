package dfcsantos.songplayer;

import sneer.bricks.hardware.cpu.lang.contracts.Contract;


public interface SongContract extends Contract {
	enum Status {NEW, PLAYING, PAUSED, DISPOSED};

	void startPlaying();
	void pauseResume();
	Status status();
}