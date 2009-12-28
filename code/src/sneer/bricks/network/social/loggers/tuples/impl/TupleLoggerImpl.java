package sneer.bricks.network.social.loggers.tuples.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.network.social.loggers.tuples.TupleLogger;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.tuples.Tuple;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.brickness.Seal;
import sneer.foundation.lang.Consumer;

class TupleLoggerImpl implements TupleLogger, Consumer<Tuple> {

	@SuppressWarnings("unused")	private final WeakContract _tupleSpaceContract;

	
	{
		_tupleSpaceContract = my(TupleSpace.class).addSubscription(Tuple.class, this);
	}

	
	@Override
	public void consume(Tuple tuple) {
		Seal publisherSeal = tuple.publisher();
		String message = my(Seals.class).ownSeal().equals(publisherSeal)
			? "Tuple published: "
			: "Tuple acquired: ";
		my(Logger.class).log(message, tuple);
	}

}
