package spikes.neo.packet;

import sneer.bricks.network.computers.connections.ByteConnection.PacketScheduler;

class PacketSchedulerMock implements PacketScheduler {

	private final String[] _messages;
	private int currentPosition = 0;

	PacketSchedulerMock(String... messages) {
		_messages = messages;
	}

	@Override
	public byte[] highestPriorityPacketToSend() {
		return _messages[currentPosition].getBytes();
	}

	@Override
	public void previousPacketWasSent() {
		currentPosition++;
	}

	public boolean hasMorePackets() {
		return currentPosition < _messages.length;
	}

}
