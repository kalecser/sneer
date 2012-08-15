package sneer.bricks.network.computers.channels.guaranteed.impl;

import java.nio.ByteBuffer;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.network.computers.channels.Channel;
import sneer.bricks.network.computers.channels.guaranteed.GuaranteedChannel;
import basis.lang.Consumer;
import basis.lang.Producer;

class GuaranteedChannelImpl implements GuaranteedChannel {

	private final Channel delegate;

	GuaranteedChannelImpl(Channel delegate) {
		this.delegate = delegate;
	}

	@Override
	public Hash id() {
		return delegate.id();
	}

	@Override
	public void open(Producer<ByteBuffer> sender, Consumer<ByteBuffer> receiver) {
		delegate.open(sender, receiver);
	}

	@Override
	public int maxPacketSize() {
		return delegate.maxPacketSize();
	}

}
