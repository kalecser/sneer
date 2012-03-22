package sneer.bricks.expression.tuples.testsupport.pump.impl;

import static basis.environments.Environments.my;
import basis.environments.Environment;
import sneer.bricks.expression.tuples.testsupport.pump.TuplePump;
import sneer.bricks.expression.tuples.testsupport.pump.TuplePumps;

class TuplePumpsImpl implements TuplePumps {

	@Override
	public TuplePump startPumpingWith(Environment otherEnviroment) {
		return new TuplePumpImpl(my(Environment.class), otherEnviroment);
	}

}
