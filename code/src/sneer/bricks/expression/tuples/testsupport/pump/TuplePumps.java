package sneer.bricks.expression.tuples.testsupport.pump;

import sneer.foundation.brickness.Brick;
import sneer.foundation.environments.Environment;

@Brick
public interface TuplePumps {

	TuplePump startPumpingWith(Environment otherEnviroment);

}
