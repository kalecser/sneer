package sneer.bricks.network.computers.udp.packet.impl;

import static java.lang.Math.ceil;
import static java.lang.Math.min;
import static java.util.Arrays.copyOfRange;
import static sneer.bricks.network.computers.udp.packet.PacketSplitter.OpCode.Checksum;
import static sneer.bricks.network.computers.udp.packet.PacketSplitter.OpCode.Piece;
import static sneer.bricks.network.computers.udp.packet.PacketSplitter.OpCode.Unique;

import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.LinkedList;

import sneer.bricks.network.computers.connections.ByteConnection.PacketScheduler;
import sneer.bricks.network.computers.udp.packet.PacketSplitter.OpCode;

class SequenceScheduler implements PacketScheduler {
	
	private final PacketScheduler scheduler;
	private final Deque<byte[]> packetsToSend = new LinkedList<byte[]>();
	private final int payloadSize;
	

	SequenceScheduler(PacketScheduler scheduler, int maxPacketSize) {
		if(maxPacketSize < 9)
			throw new IllegalStateException("Packet size should be greater than 9 (8 bytes for long checksum + 1 for opcode)");
		
		this.scheduler = scheduler;
		this.payloadSize = maxPacketSize - 1;
	}
	

	@Override
	public byte[] highestPriorityPacketToSend() {
		if(!packetsToSend.isEmpty()) return packetsToSend.peek();
		splitHighestPackets();
		return packetsToSend.peek();		
	}
	

	private void splitHighestPackets() {
		byte[] packet = scheduler.highestPriorityPacketToSend();
		double numberOfPackets = ceil(packet.length / (double) payloadSize);
				
		if (numberOfPackets == 1) {
			packetsToSend.add(prepare(Unique, packet));
			return;
		}

		for (int i = 0; i < numberOfPackets; i++) {
			int from = i * payloadSize;
			int to = from + payloadSize;
			byte[] payload = copyOfRange(packet, from, min(to, packet.length));
			
			packetsToSend.add(prepare(Piece, payload));
		}
		
		byte[] sum = checksum(packet);
		packetsToSend.add(prepare(Checksum, sum));
	}
	

	private byte[] prepare(OpCode type, byte[] payload) {
		ByteBuffer buffer = ByteBuffer.allocate(payload.length + 1);
		buffer.put((byte) type.ordinal());
		buffer.put(payload);
		return buffer.array();
	}

	
	private byte[] checksum(byte[] packet) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		
		long sum = 0;		
		for(int i = 1 ; i < packet.length; i++) sum += packet[i] * i;
		buffer.putLong(sum);
		
		return buffer.array();
	}
	

	@Override
	public void previousPacketWasSent() {
		if (!packetsToSend.isEmpty())
			packetsToSend.pollFirst();
		
		if (packetsToSend.isEmpty())
			scheduler.previousPacketWasSent();
	}
}