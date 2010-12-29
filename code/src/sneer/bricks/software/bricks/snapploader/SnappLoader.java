package sneer.bricks.software.bricks.snapploader;

import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.foundation.brickness.Brick;

@Brick
public interface SnappLoader {

	Latch loadingFinished();
	
	boolean wereThrowablesCaughtWhenLoadingSnapps();

}
