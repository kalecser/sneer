package dfcsantos.tracks.player;

import sneer.bricks.hardware.cpu.lang.contracts.Contract;


public interface TrackContract extends Contract {

	void pauseResume();

	int trackElapsedTime();

}
