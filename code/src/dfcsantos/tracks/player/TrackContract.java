package dfcsantos.tracks.player;

import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.pulp.reactive.Signal;


public interface TrackContract extends Contract {

	void pauseResume();

	int trackElapsedTime();

	Signal<Boolean> isPaused();
}
