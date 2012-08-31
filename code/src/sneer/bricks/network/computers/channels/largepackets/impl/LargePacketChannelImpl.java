package sneer.bricks.network.computers.channels.largepackets.impl;

import static basis.environments.Environments.my;

import java.nio.ByteBuffer;

import sneer.bricks.network.computers.channels.Channel;
import sneer.bricks.network.computers.channels.guaranteed.splitter.PacketSplitters;
import sneer.bricks.network.computers.channels.largepackets.LargePacketChannel;
import sneer.bricks.pulp.reactive.Signal;
import basis.lang.Consumer;
import basis.lang.Producer;

class LargePacketChannelImpl implements LargePacketChannel {

	private final Channel delegate;

	LargePacketChannelImpl(Channel delegate) {
		this.delegate = delegate;
	}
	

	@Override
	public long id() {
		return delegate.id();
	}
	

	@Override
	public void open(Producer<? extends ByteBuffer> sender, Consumer<? super ByteBuffer> receiver) {
		delegate.open(splitter(sender), joiner(receiver));
	}
	

	@Override
	public int maxPacketSize() {
		return delegate.maxPacketSize() * PacketSplitters.MAX_PIECES;
	}
	
	
	private Consumer<? super ByteBuffer> joiner(Consumer<? super ByteBuffer> receiver) {
		return my(PacketSplitters.class).newJoiner(receiver);
	}
	

	private Producer<? extends ByteBuffer> splitter(Producer<? extends ByteBuffer> sender) {
		return my(PacketSplitters.class).newSplitter(sender, delegate.maxPacketSize());
	}


	@Override
	public Signal<Boolean> isUp() {
		return delegate.isUp();
	}
	


}
