package sneer.bricks.expression.tuples.testsupport.pump.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.expression.tuples.testsupport.pump.TuplePump;
import sneer.bricks.expression.tuples.testsupport.pump.TuplePumps;
import sneer.foundation.environments.Environment;

class TuplePumpsImpl implements TuplePumps {

	@Override
	public TuplePump startPumpingWith(Environment otherEnviroment) {
		return new TuplePumpImpl(my(Environment.class), otherEnviroment);
	}

}
