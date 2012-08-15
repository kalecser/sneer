package sneer.bricks.network.computers.channels.impl;

import java.nio.ByteBuffer;

import sneer.bricks.network.computers.channels.Channel;
import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.udp.UdpNetwork;
import basis.lang.Consumer;
import basis.lang.Producer;

class ChannelImpl implements Channel {

	private static final int BYTES_TO_REPRESENT_ID = 4;
	
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
	public void open(final Producer<ByteBuffer> sender, final Consumer<ByteBuffer> receiver) {
		connection.initCommunications(new ByteConnection.PacketScheduler() {
			
			@Override
			public void previousPacketWasSent() {				
			}
			
			@Override
			public byte[] highestPriorityPacketToSend() {
				ByteBuffer packet = sender.produce();
				byte[] ret = new byte[BYTES_TO_REPRESENT_ID + packet.remaining()];
				putIdIn(ret);
				packet.get(ret, BYTES_TO_REPRESENT_ID, packet.remaining());
				return ret;
			}
		}, new Consumer<byte[]>() { @Override public void consume(byte[] value) {
			receiver.consume(ByteBuffer.wrap(value, BYTES_TO_REPRESENT_ID, value.length - BYTES_TO_REPRESENT_ID));
		}});
	}

	
	@Override
	public int maxPacketSize() {
		return UdpNetwork.MAX_PACKET_PAYLOAD_SIZE - BYTES_TO_REPRESENT_ID;
	}

	
	void putIdIn(byte[] array) {
		ByteBuffer.wrap(array).putLong(id);
	}

}
