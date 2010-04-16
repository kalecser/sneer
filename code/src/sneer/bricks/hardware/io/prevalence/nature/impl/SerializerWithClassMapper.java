package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.prevayler.TransactionWithQuery;
import org.prevayler.foundation.serialization.Serializer;

import sneer.bricks.hardware.io.log.Logger;
import sneer.foundation.brickness.BrickSerializationMapper;


class SerializerWithClassMapper implements Serializer {

	private static final sneer.bricks.pulp.serialization.Serializer Serializer = my(sneer.bricks.pulp.serialization.Serializer.class);
	
	@Override public void writeObject(OutputStream stream, Object object) throws IOException {
		Serializer.serialize(stream, object, my(BrickSerializationMapper.class));
	}
	
	@Override public Object readObject(InputStream stream) throws IOException {
		try {
			return Serializer.deserialize(stream, my(BrickSerializationMapper.class));
		} catch (ClassNotFoundException e) {
			my(Logger.class).log("Transaction skipped. Class not found: ", e.getMessage());
			return transactionSkip();
		}
	}

	private Object transactionSkip() {
		return new TransactionWithQuery() { @Override public Object executeAndQuery(Object prevalentSystem, Date executionTime) {
			return null;
		}};
	}
}
