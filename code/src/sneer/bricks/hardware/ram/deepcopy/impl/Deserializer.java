package sneer.bricks.hardware.ram.deepcopy.impl;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.io.InputStream;

import sneer.bricks.pulp.serialization.Serializer;


class Deserializer extends Thread {

	static private final Serializer Serializer = my(Serializer.class);

	
	private final InputStream _inputStream;
	
	private Object _result;
	private RuntimeException _unexpectedException;

	
	Deserializer(InputStream inputStream) {
		_inputStream = inputStream;
		setDaemon(true);
	}

	
	@Override
	public void run() {
		try {
			_result = Serializer.deserialize(_inputStream);
		} catch (Throwable t) {
			_unexpectedException = new RuntimeException(t);
		}
			
		readAnyTrailingBytesWrittenBySillySerializers();
	}

	
	private void readAnyTrailingBytesWrittenBySillySerializers() {
		try {
			while (_inputStream.read() != -1) {}
		} catch (IOException e) {
			// The object has already been successfully deserialized anyway.
		}
	}

	
	Object getResult() throws InterruptedException {
		join();
		if (_unexpectedException != null) throw _unexpectedException;
		return _result;
	}
}