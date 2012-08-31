package sneer.bricks.network.computers.channels.guaranteed.impl;

import java.nio.ByteBuffer;

import sneer.bricks.network.computers.channels.Channel;
import sneer.bricks.network.computers.channels.guaranteed.GuaranteedChannel;
import sneer.bricks.pulp.reactive.Signal;
import basis.lang.Consumer;
import basis.lang.Producer;

class GuaranteedChannelImpl implements GuaranteedChannel {

	private final Channel delegate;

	GuaranteedChannelImpl(Channel delegate) {
		this.delegate = delegate;
	}

	@Override
	public long id() {
		return delegate.id();
	}

	@Override
	public void open(Producer<? extends ByteBuffer> sender, Consumer<? super ByteBuffer> receiver) {
		delegate.open(sender, receiver);
	}

	@Override
	public int maxPacketSize() {
		return delegate.maxPacketSize();
	}

	@Override
	public Signal<Boolean> isUp() {
		return delegate.isUp();
	}

}
