package sneer.bricks.network.computers.udp.packet.impl;

import static java.lang.Math.ceil;
import static java.lang.Math.min;
import static java.util.Arrays.copyOfRange;

import java.util.Deque;
import java.util.LinkedList;

import sneer.bricks.network.computers.connections.ByteConnection.PacketScheduler;

class SequenceScheduler implements PacketScheduler {
	
	private final PacketScheduler scheduler;
	private final Deque<byte[]> packetsToSend = new LinkedList<byte[]>();
	private final int payloadSize;

	SequenceScheduler(PacketScheduler scheduler, int payloadSize) {
		this.scheduler = scheduler;
		this.payloadSize = payloadSize;
	}

	@Override
	public byte[] highestPriorityPacketToSend() {
		if(!packetsToSend.isEmpty()) return packetsToSend.peek();
		splitPackets();
		return packetsToSend.peek();
	}

	private void splitPackets() {
		byte[] packet = scheduler.highestPriorityPacketToSend();
		double numberOfPackets = ceil(packet.length / (double) payloadSize);

		for (int i = 0; i < numberOfPackets; i++) {
			int from = i * payloadSize;
			int to = from + payloadSize;
			packetsToSend.add(copyOfRange(packet, from, min(to, packet.length)));
		}
	}

	@Override
	public void previousPacketWasSent() {
		if (!packetsToSend.isEmpty())
			packetsToSend.pollFirst();
		
		if (packetsToSend.isEmpty())
			scheduler.previousPacketWasSent();
	}
}