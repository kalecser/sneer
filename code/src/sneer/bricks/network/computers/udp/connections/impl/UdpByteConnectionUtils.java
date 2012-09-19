package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;
import static sneer.bricks.network.computers.udp.UdpNetwork.MAX_PACKET_PAYLOAD_SIZE;

import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

import sneer.bricks.hardware.io.log.exceptions.ExceptionLogger;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.network.computers.udp.connections.UdpPacketType;
import sneer.bricks.network.computers.udp.sender.UdpSender;

class UdpByteConnectionUtils {
	
	private static final UdpSender sender = my(UdpSender.class);

	
	static ByteBuffer prepare(UdpPacketType type) {
		ByteBuffer ret = ByteBuffer.wrap(new byte[MAX_PACKET_PAYLOAD_SIZE]);
		ret.put((byte)type.ordinal());
		ret.put(ownSealBytes());
		return ret;
	}
	
	
	static void send(ByteBuffer data, SocketAddress peerAddress) {
		DatagramPacket packet = packetFor(data, peerAddress);
		if (packet == null) return;
		
		sender.send(packet);
	}

	
	private static DatagramPacket packetFor(ByteBuffer data, SocketAddress peerAddress) {
		if (peerAddress == null) return null;
		try {
			return new DatagramPacket(data.array(), data.limit(), peerAddress); //Optimize: reuse DatagramPacket
		} catch (SocketException e) {
			my(ExceptionLogger.class).log(e);
			return null;
		}
	}

	
	private static byte[] ownSealBytes() {
		return my(OwnSeal.class).get().currentValue().bytes.copy();
	}

}
