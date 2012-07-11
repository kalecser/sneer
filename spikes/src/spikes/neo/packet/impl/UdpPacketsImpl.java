package spikes.neo.packet.impl;

import static java.lang.Math.ceil;
import static java.lang.Math.min;
import static java.util.Arrays.copyOfRange;

import java.util.LinkedList;
import java.util.List;

import sneer.bricks.network.computers.connections.ByteConnection.PacketScheduler;
import spikes.neo.packet.UdpPackets;
import basis.lang.Consumer;

public class UdpPacketsImpl implements UdpPackets {

	@Override
	public PacketScheduler splitScheduler(PacketScheduler scheduler, int payloadSize) {
		return new SequenceScheduler(scheduler, payloadSize);
	}

	@Override
	public Consumer<byte[]> joinReceiver(Consumer<byte[]> receiver) {
		return receiver;
	}
	
	static private class SequenceScheduler implements PacketScheduler {
		
		private final PacketScheduler _scheduler;
		private final List<byte[]> packetsToSend = new LinkedList<byte[]>();
		private final int _payloadSize;

		SequenceScheduler(PacketScheduler scheduler, int payloadSize) {
			_scheduler = scheduler;
			_payloadSize = payloadSize;
		}

		@Override
		public byte[] highestPriorityPacketToSend() {
			if(!packetsToSend.isEmpty()) return packetsToSend.get(0);
			
			byte[] packet = _scheduler.highestPriorityPacketToSend();
			double numberOfPackets = ceil(packet.length / (double)_payloadSize);
			
			for (int i = 0; i < numberOfPackets; i++) {
				int from = i * _payloadSize;
				int to = from + _payloadSize;
				packetsToSend.add(copyOfRange(packet, from, min(to, packet.length)));
			}
			
			return highestPriorityPacketToSend();
		}

		@Override
		public void previousPacketWasSent() {
			if (!packetsToSend.isEmpty())
				packetsToSend.remove(0);
			
			if (packetsToSend.isEmpty())
				_scheduler.previousPacketWasSent();
		}
		
	}

}
