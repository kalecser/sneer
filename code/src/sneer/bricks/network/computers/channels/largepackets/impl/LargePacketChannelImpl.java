package sneer.bricks.network.computers.channels.largepackets.impl;

import static basis.environments.Environments.my;

import java.nio.ByteBuffer;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.network.computers.channels.Channel;
import sneer.bricks.network.computers.channels.guaranteed.splitter.PacketSplitters;
import sneer.bricks.network.computers.channels.largepackets.LargePacketChannel;
import basis.lang.Consumer;
import basis.lang.Producer;

class LargePacketChannelImpl implements LargePacketChannel {

	private final Channel delegate;

	LargePacketChannelImpl(Channel delegate) {
		this.delegate = delegate;
	}
	

	@Override
	public Hash id() {
		return delegate.id();
	}
	

	@Override
	public void open(Producer<ByteBuffer> sender, Consumer<ByteBuffer> receiver) {
		delegate.open(splitter(sender), joiner(receiver));
	}
	

	@Override
	public int maxPacketSize() {
		return delegate.maxPacketSize() * PacketSplitters.MAX_PIECES;
	}
	
	
	private Consumer<ByteBuffer> joiner(Consumer<ByteBuffer> receiver) {
		return my(PacketSplitters.class).newJoiner(receiver);
	}
	

	private Producer<ByteBuffer> splitter(Producer<ByteBuffer> sender) {
		return my(PacketSplitters.class).newSplitter(sender, delegate.maxPacketSize());
	}
	


}
