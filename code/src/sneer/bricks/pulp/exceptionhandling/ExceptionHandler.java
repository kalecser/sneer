package sneer.bricks.pulp.exceptionhandling;

import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.ClosureX;

@Brick
public interface ExceptionHandler {

	void shield(Runnable runnable);
	void shieldX(ClosureX<?> closure);

}