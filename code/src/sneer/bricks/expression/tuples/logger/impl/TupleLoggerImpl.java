package sneer.bricks.expression.tuples.logger.impl;

import static basis.environments.Environments.my;
import basis.lang.Consumer;
import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.logger.TupleLogger;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;

class TupleLoggerImpl implements TupleLogger, Consumer<Tuple> {

	@SuppressWarnings("unused")	private final WeakContract _tupleSpaceContract;

	
	{
		_tupleSpaceContract = my(TupleSpace.class).addSubscription(Tuple.class, this);
	}

	
	@Override
	public void consume(Tuple tuple) {
		Seal publisherSeal = tuple.publisher;
		String message = my(OwnSeal.class).get().currentValue().equals(publisherSeal)
			? "Tuple published: "
			: "Tuple acquired: ";
		my(Logger.class).log(message, tuple);
	}

}
