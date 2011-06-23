package sneer.bricks.software.bricks.snapploader;

import sneer.foundation.brickness.Brick;
import sneer.foundation.util.concurrent.Latch;

@Brick
public interface SnappLoader {

	Latch loadingFinished();
	
	boolean wereThrowablesCaughtWhenLoadingSnapps();

}
