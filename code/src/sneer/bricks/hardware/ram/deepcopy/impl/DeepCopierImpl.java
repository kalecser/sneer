package sneer.bricks.hardware.ram.deepcopy.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import sneer.bricks.hardware.ram.deepcopy.DeepCopier;
import sneer.bricks.pulp.serialization.Serializer;

class DeepCopierImpl implements DeepCopier {

	static private final Serializer Serializer = my(Serializer.class);

	
	@Override
	public <T> T deepCopy(T original) {
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			Serializer.serialize(byteOut, original);
			ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
			return (T) Serializer.deserialize(byteIn);
		} catch (Exception shouldNeverHappen) {
			throw new IllegalStateException(shouldNeverHappen);
		}
	}


	@Override
	public Object deepCopyThroughPipe(Object original) {
		try {
			return tryToDeepCopyThroughPipe(original);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	
	private Object tryToDeepCopyThroughPipe(Object original) throws IOException, InterruptedException {
		PipedOutputStream outputStream = new PipedOutputStream();
		PipedInputStream inputStream = new PipedInputStream(outputStream);

		Deserializer consumer = new Deserializer(inputStream);
		consumer.start();

		try {
			Serializer.serialize(outputStream, original);
		} finally {
			outputStream.close();
		}

		return consumer.getResult();
	}
}
