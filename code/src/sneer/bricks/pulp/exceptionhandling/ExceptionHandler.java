package sneer.bricks.pulp.exceptionhandling;

import basis.brickness.Brick;
import basis.lang.ClosureX;

@Brick
public interface ExceptionHandler {

	void shield(Runnable runnable);
	void shieldX(ClosureX<?> closure);

}