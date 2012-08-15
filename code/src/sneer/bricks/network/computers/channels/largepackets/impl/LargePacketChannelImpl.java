package sneer.bricks.network.computers.channels.largepackets.impl;

import static basis.environments.Environments.my;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.computers.channels.Channel;
import sneer.bricks.network.computers.channels.guaranteed.splitter.PacketSplitter;
import sneer.bricks.network.computers.channels.guaranteed.splitter.PacketSplitters;
import sneer.bricks.network.computers.channels.largepackets.LargePacketChannel;
import basis.lang.Consumer;
import basis.lang.Producer;

class LargePacketChannelImpl implements LargePacketChannel, Producer<ByteBuffer>, Consumer<ByteBuffer> {

	private final Channel delegate;
	private final PacketSplitter splitter;
	private Producer<ByteBuffer> sender;
	private Iterator<ByteBuffer> piecesToSend = Collections.EMPTY_LIST.iterator();
	@SuppressWarnings("unused") private WeakContract ref;

	LargePacketChannelImpl(Channel delegate) {
		this.delegate = delegate;
		splitter = my(PacketSplitters.class).newInstance(this.delegate.maxPacketSize());
	}

	@Override
	public Hash id() {
		return delegate.id();
	}

	@Override
	public void open(Producer<ByteBuffer> sender, Consumer<ByteBuffer> receiver) {
		this.sender = sender;
		ref = splitter.lastJoinedPacket().addReceiver(receiver);
		delegate.open(this, this);
	}

	@Override
	public int maxPacketSize() {
		return delegate.maxPacketSize() * PacketSplitter.MAX_PIECES;
	}

	@Override
	public ByteBuffer produce() {
		if (!piecesToSend.hasNext()) {
			ByteBuffer[] pieces = splitter.split(sender.produce());
			piecesToSend = Arrays.asList(pieces).iterator();
		}
		
		return piecesToSend.next();
	}

	@Override
	public void consume(ByteBuffer piece) {
		splitter.join(piece);
	}

}
