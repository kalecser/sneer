package sneer.bricks.network.computers.udp.packet.tests;

import sneer.bricks.network.computers.connections.ByteConnection.PacketScheduler;

class PacketSchedulerMock implements PacketScheduler {

	private final String[] messages;
	private int currentPosition = 0;

	PacketSchedulerMock(String... messages) {
		this.messages = messages;
	}

	@Override
	public byte[] highestPriorityPacketToSend() {
		return messages[currentPosition].getBytes();
	}

	@Override
	public void previousPacketWasSent() {
		currentPosition++;
	}

	public boolean hasMorePackets() {
		return currentPosition < messages.length;
	}

}
