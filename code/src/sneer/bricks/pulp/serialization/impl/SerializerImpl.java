package sneer.bricks.pulp.serialization.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sneer.bricks.pulp.serialization.Serializer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.binary.BinaryStreamReader;
import com.thoughtworks.xstream.io.binary.BinaryStreamWriter;

class SerializerImpl implements Serializer {
	
	@Override
	public byte[] serialize(Object object) {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		try {
			serialize(result, object);
		} catch (IOException e) {
			throw new IllegalStateException("Failed to serialize: " + object, e);
		}
		return result.toByteArray();
	}
	
	
	@Override
	public void serialize(OutputStream stream, Object object) throws IOException {
		XStream xstream = XStreamPool.borrowWorker();
		try {
			serializeWith(stream, object, xstream);
		} finally {
			XStreamPool.returnWorker(xstream);
		}
	}


	private void serializeWith(OutputStream stream, Object object, XStream xstream) throws IOException {
		try {
			BinaryStreamWriter writer = new BinaryStreamWriter(stream);
			xstream.marshal(object, writer);
			writer.flush();
		} catch (RuntimeException rx) {
			throw new IOException(rx);
		}
	}

	
	@Override
	public Object deserialize(byte[] serializedValue) throws ClassNotFoundException {
		try {
			return deserialize(new ByteArrayInputStream(serializedValue));
		} catch (IOException ioe) {
			throw new IllegalStateException(ioe);
		}
	}
	
	
	@Override
	public Object deserialize(final InputStream stream) throws IOException, ClassNotFoundException {
		XStream xstream = XStreamPool.borrowWorker();
		try {
			return deserializeWith(stream, xstream);
		} finally {
			XStreamPool.returnWorker(xstream);
		}
	}


	private Object deserializeWith(InputStream stream, XStream xstream)	throws ClassNotFoundException, IOException {
		try {
			return xstream.unmarshal(new BinaryStreamReader(stream));
		} catch (RuntimeException rx) {
			Throwable cause = rx;
			while (cause != null) {
				if (cause instanceof ClassNotFoundException)
					throw (ClassNotFoundException)cause;
				cause = cause.getCause();
			}
			
			throw new IOException(rx);
		}
	}
	
}
