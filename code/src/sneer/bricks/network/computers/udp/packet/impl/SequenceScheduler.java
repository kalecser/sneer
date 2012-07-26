package sneer.bricks.network.computers.udp.packet.impl;

import static java.lang.Math.ceil;
import static java.lang.Math.min;
import static java.util.Arrays.copyOfRange;
import static sneer.bricks.network.computers.udp.packet.PacketSplitter.OpCode.First;
import static sneer.bricks.network.computers.udp.packet.PacketSplitter.OpCode.Piece;
import static sneer.bricks.network.computers.udp.packet.PacketSplitter.OpCode.Unique;

import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.LinkedList;

import sneer.bricks.network.computers.connections.ByteConnection.PacketScheduler;
import sneer.bricks.network.computers.udp.packet.PacketSplitter.OpCode;

class SequenceScheduler implements PacketScheduler {
	
	private static final int HEADER_SIZE = 3;
	
	private final PacketScheduler scheduler;
	private final Deque<byte[]> packetsToSend = new LinkedList<byte[]>();
	private final int payloadSize;
	private byte group = 0;
	

	SequenceScheduler(PacketScheduler scheduler, int maxPacketSize) {
		this.scheduler = scheduler;
		this.payloadSize = maxPacketSize;
	}
	

	@Override
	public byte[] highestPriorityPacketToSend() {
		if(!packetsToSend.isEmpty()) return packetsToSend.peek();
		splitHighestPacket();
		return packetsToSend.peek();		
	}
	

	private void splitHighestPacket() {
		byte[] packet = scheduler.highestPriorityPacketToSend();
				
		if (packet.length + 1 <= payloadSize) {
			uniquePacket(packet);
			return;
		}
		
		multiplePackets(packet);
	}


	private void multiplePackets(byte[] packet) {
		int numberOfPackets = (int) ceil(packet.length / (double)(payloadSize - HEADER_SIZE));
		
		group++;
		
		for (int piece = 0; piece < numberOfPackets; piece++)
			cropPacket(packet, numberOfPackets, piece);
	}


	private void cropPacket(byte[] packet, int numberOfPackets, int piece) {
		int from = piece * (payloadSize - HEADER_SIZE);
		int to = from + payloadSize - HEADER_SIZE;
		byte[] payload = copyOfRange(packet, from, min(to, packet.length));

		OpCode opCode = piece == 0 ? First : Piece;

		ByteBuffer buffer = prepare(opCode, payload.length + HEADER_SIZE);
		buffer.put(group);
		
		if (opCode == First)
			buffer.put((byte) (numberOfPackets - 1));
		else
			buffer.put((byte)piece);
			
		buffer.put(payload);

		packetsToSend.add(buffer.array());
	}


	private void uniquePacket(byte[] packet) {
		ByteBuffer buffer = prepare(Unique, packet.length + 1);
		buffer.put(packet);
		packetsToSend.add(buffer.array());
	}
	
	
	private ByteBuffer prepare(OpCode type, int size) {
		ByteBuffer buffer = ByteBuffer.allocate(size);
		buffer.put((byte) type.ordinal());
		return buffer;
	}
	
	
	@Override
	public void previousPacketWasSent() {
		if (!packetsToSend.isEmpty())
			packetsToSend.pollFirst();
		
		if (packetsToSend.isEmpty())
			scheduler.previousPacketWasSent();
	}
}