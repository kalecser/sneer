package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.prevayler.TransactionWithQuery;
import org.prevayler.foundation.serialization.Serializer;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.pulp.serialization.ClassMapper;
import sneer.foundation.brickness.BrickClassLoader;


class SerializerWithClassMapper implements Serializer, ClassMapper {

	private static final sneer.bricks.pulp.serialization.Serializer Serializer = my(sneer.bricks.pulp.serialization.Serializer.class);
	
	@Override public void writeObject(OutputStream stream, Object object) throws IOException {
		Serializer.serialize(stream, object, this);
	}
	
	@Override public Object readObject(InputStream stream) throws IOException {
		try {
			return Serializer.deserialize(stream, this);
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

	@Override
	public Class<?> classGiven(String serializationHandle) throws ClassNotFoundException {
		String[] parts = serializationHandle.split(":");
		switch (parts.length) {
		case 2: // api:class
			return Prevalent.class.getClassLoader().loadClass(parts[1]);
		case 3: // brick:kind:class
			return getClass().getClassLoader().loadClass(parts[2]);
		default:
			throw new IllegalStateException();
		}
	}

	@Override
	public String serializationHandleFor(Class<?> klass) {
		return classLoaderPrefixFor(klass) + ":" + klass.getName();
	}

	private String classLoaderPrefixFor(Class<?> klass) {
		ClassLoader loader = klass.getClassLoader();
		if (loader instanceof BrickClassLoader) {
			BrickClassLoader brickClassLoader = (BrickClassLoader)loader;
			Class<?> brick = brickClassLoader.brick();
			String kind = brickClassLoader.kind().toString().toLowerCase();
			
			// brick:kind
			return brick.getName() + ":" + kind;
		}
		return "api";
	}
}
