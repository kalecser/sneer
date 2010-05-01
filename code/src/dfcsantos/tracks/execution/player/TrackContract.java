package dfcsantos.tracks.execution.player;

import sneer.bricks.hardware.cpu.lang.contracts.Contract;


public interface TrackContract extends Contract {

	int trackElapsedTime();

	void volumePercent(int level);

}
