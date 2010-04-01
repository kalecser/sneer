package sneer.bricks.pulp.serialization.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sneer.bricks.pulp.serialization.ClassMapper;
import sneer.bricks.pulp.serialization.Serializer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.binary.BinaryStreamReader;
import com.thoughtworks.xstream.io.binary.BinaryStreamWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

class SerializerImpl implements Serializer {
	
	@Override
	public void serialize(OutputStream stream, Object object) throws IOException {
		serializeWith(stream, object, new XStream());
	}


	private void serializeWith(OutputStream stream, Object object,
			XStream xstream) throws IOException {
		try {
			BinaryStreamWriter writer = new BinaryStreamWriter(stream);
			xstream.marshal(object, writer);
			writer.flush();
		} catch (RuntimeException rx) {
			throw new IOException(rx);
		}
	}

	
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
	public Object deserialize(InputStream stream, ClassLoader classLoader) throws IOException, ClassNotFoundException {
		return deserializeWith(stream, xStreamWith(classLoader));
	}

	private Object deserializeWith(InputStream stream, XStream xstream)
			throws ClassNotFoundException, IOException {
		try {
			return xstream.unmarshal(new BinaryStreamReader(stream));
		} catch (RuntimeException rx) {
			Throwable cause = rx;
			while (cause != null) {
				if (cause instanceof CannotResolveClassException)
					throw new ClassNotFoundException(cause.getMessage());
				cause = cause.getCause();
			}
			
			throw new IOException(rx);
		}
	}


	private XStream xStreamWith(ClassLoader classLoader) {
		XStream xs = new XStream(); // XStream instances are not thread safe
		xs.setClassLoader(classLoader);
		return xs;
	}


	@Override
	public Object deserialize(byte[] bytes, ClassLoader classloader) throws ClassNotFoundException {
		try {
			return deserialize(new ByteArrayInputStream(bytes), classloader);
		} catch (IOException ioe) {
			throw new IllegalStateException(ioe);
		}
	}


	@Override
	public Object deserialize(InputStream stream, final ClassMapper mapper)
			throws IOException, ClassNotFoundException {
		return deserializeWith(stream, xStreamWith(mapper));
	}


	@SuppressWarnings("deprecation")
	private XStream xStreamWith(final ClassMapper mapper) {
		Mapper m = new MapperWrapper(new XStream().getMapper()) {
			@SuppressWarnings("unchecked")
			@Override
			public String serializedClass(Class type) {
				return mapper.serializationHandleFor(type);
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public Class realClass(String elementName) {
				try {
					return mapper.classGiven(elementName);
				} catch (Exception e) {
					return super.realClass(elementName);
				}
			}
		};
		return new XStream(null, m, new XppDriver());
	}


	@Override
	public void serialize(OutputStream stream, Object obj, ClassMapper mapper) throws IOException {
		serializeWith(stream, obj, xStreamWith(mapper));
	}

}