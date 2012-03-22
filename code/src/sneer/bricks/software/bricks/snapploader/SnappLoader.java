package sneer.bricks.software.bricks.snapploader;

import basis.brickness.Brick;
import basis.util.concurrent.Latch;

@Brick
public interface SnappLoader {

	Latch loadingFinished();
	
	boolean wereThrowablesCaughtWhenLoadingSnapps();

}
