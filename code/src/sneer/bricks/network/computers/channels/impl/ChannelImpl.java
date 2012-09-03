package sneer.bricks.network.computers.channels.impl;

import java.nio.ByteBuffer;

import sneer.bricks.network.computers.channels.Channel;
import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.udp.UdpNetwork;
import sneer.bricks.pulp.reactive.Signal;
import basis.lang.Consumer;
import basis.lang.Producer;

class ChannelImpl implements Channel {

	private static final int BYTES_TO_REPRESENT_ID = 8;
	
	private final long id;
	private final ByteConnection connection;

	ChannelImpl(ByteConnection connection) {
		this.connection = connection;
		id = 0;
	}

	@Override
	public long id() {
		return id;
	}

	@Override
	public void open(final Producer<? extends ByteBuffer> sender, final Consumer<? super ByteBuffer> receiver) {
		connection.initCommunications(new Producer<ByteBuffer>() { @Override public ByteBuffer produce() {
				ByteBuffer payload = sender.produce();
				ByteBuffer ret = ByteBuffer.allocate(BYTES_TO_REPRESENT_ID + payload.remaining());
				ret.putLong(id);
				ret.put(payload);
				ret.flip();
				return ret;
			}
		}, new Consumer<ByteBuffer>() { @Override public void consume(ByteBuffer packet) {
			checkId(packet.getLong());
			receiver.consume(packet);
		}});
	}

	
	@Override
	public int maxPacketSize() {
		return UdpNetwork.MAX_PACKET_PAYLOAD_SIZE - BYTES_TO_REPRESENT_ID;
	}

	
	@Override
	public Signal<Boolean> isUp() {
		return connection.isConnected();
	}

	
	void checkId(long actual) {
		if (id != actual)
			throw new IllegalStateException("Channel ID should be " + id + " but was " + actual);
	}

}
