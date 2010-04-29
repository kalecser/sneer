package sneer.bricks.pulp.serialization.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sneer.bricks.pulp.serialization.Serializer;
import sneer.foundation.brickness.BrickSerializationMapper;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.binary.BinaryStreamReader;
import com.thoughtworks.xstream.io.binary.BinaryStreamWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

class SerializerImpl implements Serializer {
	
	private ThreadLocal<XStream> _xstreams = new ThreadLocal<XStream>() {
		@Override
		protected XStream initialValue() {
			return createXStream();
		}
	};

	@Override
	public void serialize(OutputStream stream, Object object) throws IOException {
		serializeWith(stream, object, xStream());
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
	public Object deserialize(InputStream stream) throws IOException, ClassNotFoundException {
		return deserializeWith(stream, xStream());
	}
	
	private XStream xStream() {
		return _xstreams.get();
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

	@Override
	public Object deserialize(byte[] serializedValue) throws ClassNotFoundException {
		try {
			return deserialize(new ByteArrayInputStream(serializedValue));
		} catch (IOException ioe) {
			throw new IllegalStateException(ioe);
		}
	}

	@SuppressWarnings("deprecation")
	private XStream createXStream() {
		Mapper m = new MapperWrapper(new XStream().getMapper()) {
			
			private BrickSerializationMapper mapper = my(BrickSerializationMapper.class);
			
			@SuppressWarnings("unchecked")
			@Override
			public String serializedClass(Class type) {
				return type != null
					? mapper.serializationHandleFor(type)
					: super.serializedClass(type);
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

}
