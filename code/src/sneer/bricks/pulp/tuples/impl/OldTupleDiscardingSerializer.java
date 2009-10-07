package sneer.bricks.pulp.tuples.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.prevayler.TransactionWithQuery;
import org.prevayler.foundation.serialization.Serializer;

import sneer.bricks.hardware.io.log.Logger;


class OldTupleDiscardingSerializer implements Serializer {

	private static final sneer.bricks.pulp.serialization.Serializer Serializer = my(sneer.bricks.pulp.serialization.Serializer.class);

	
	@Override public void writeObject(OutputStream stream, Object object) throws IOException {
		Serializer.serialize(stream, object);
	}
	
	
	@Override public Object readObject(InputStream stream) throws IOException {
		try {
			return Serializer.deserialize(stream, TupleSpaceImpl.class.getClassLoader());
		} catch (ClassNotFoundException e) {
			return transactionSkip(e);
		}
	}


	private Object transactionSkip(final ClassNotFoundException e) {
		return new TransactionWithQuery() {	@Override public Object executeAndQuery(Object prevalentSystem, Date executionTime) {
			my(Logger.class).log("Transaction skipped. Class not found: ", e.getMessage());
			return null;
		}};
	}

}
