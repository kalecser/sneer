package sneer.bricks.network.computers.udp.connections.tests;

import static basis.environments.Environments.my;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.network.computers.connections.ByteConnection.PacketScheduler;

final class PacketSchedulerMock implements PacketScheduler {
	final String[] messages;
	int next = 0;

	public PacketSchedulerMock(String... messages){
		this.messages = messages;
	}
	
	@Override
	public void previousPacketWasSent() {
		next++;
	}

	@Override
	public synchronized byte[] highestPriorityPacketToSend() {
		blockIfFinished();
		return messages[next].getBytes();
	}

	private void blockIfFinished() {
		if (next == messages.length) my(Threads.class).waitWithoutInterruptions(this);
	}
}