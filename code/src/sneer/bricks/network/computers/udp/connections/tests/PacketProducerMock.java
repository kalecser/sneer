package sneer.bricks.network.computers.udp.connections.tests;

import static basis.environments.Environments.my;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import sneer.bricks.hardware.cpu.threads.Threads;
import basis.lang.Producer;

final class PacketProducerMock implements Producer<ByteBuffer> {
	
	private static final Charset UTF8 = Charset.forName("UTF-8");
	
	final String[] messages;
	int next = 0;

	public PacketProducerMock(String... messages){
		this.messages = messages;
	}
	
	@Override
	public ByteBuffer produce() {
		try {
			return ByteBuffer.wrap(highestPriorityPacketToSend());
		} finally {
			previousPacketWasSent();
		}
	}

	public void previousPacketWasSent() {
		next++;
	}

	public synchronized byte[] highestPriorityPacketToSend() {
		blockIfFinished();
		return messages[next].getBytes(UTF8);
	}

	private void blockIfFinished() {
		if (next == messages.length) my(Threads.class).waitWithoutInterruptions(this);
	}

}