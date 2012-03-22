package sneer.bricks.pulp.retrier;

import basis.brickness.Brick;

@Brick
public interface RetrierManager {

	Retrier startRetrier(int periodBetweenAttempts, Task task);

}
