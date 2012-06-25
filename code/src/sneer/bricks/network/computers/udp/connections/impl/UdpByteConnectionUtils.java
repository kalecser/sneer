package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.net.SocketException;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.io.log.exceptions.ExceptionLogger;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.network.computers.udp.connections.UdpPacketType;
import sneer.bricks.network.computers.udp.sender.UdpSender;

class UdpByteConnectionUtils {
	
	private static final UdpSender sender = my(UdpSender.class);

	//	static boolean send(UdpPacketType type, byte[] payload, SocketAddress peerAddress) {
//		ByteBuffer buf = ByteBuffer.wrap(new byte[UdpNetwork.MAX_PACKET_PAYLOAD_SIZE]);
//		buf.put((byte)type.ordinal());
//		buf.put(ownSealBytes());
//		buf.put(payload);
//		
//		DatagramPacket packet = packetFor(buf, peerAddress);
//		if(packet == null) return false;
//		
//		sender.send(packet);
//		return true;
//	}
//
//	private static DatagramPacket packetFor(ByteBuffer buf, SocketAddress peerAddress) {
//		if (peerAddress == null) return null;
//		try {
//			return new DatagramPacket(buf.array(), buf.position(), peerAddress); //Optimize: reuse DatagramPacket
//		} catch (SocketException e) {
//			my(ExceptionLogger.class).log(e);
//			return null;
//		}
//	}

	static boolean send(UdpPacketType type, byte[] payload, SocketAddress peerAddress) {
		byte[] ownSeal = ownSealBytes();
		byte[] typeByte = new byte[] { (byte)type.ordinal() };
		
		byte[] data = my(Lang.class).arrays().concat(typeByte, ownSeal);
		data = my(Lang.class).arrays().concat(data, payload); //Optimize: Reuse array.
		
		DatagramPacket packet = packetFor(data, peerAddress);
		if(packet == null) return false;
		
		sender.send(packet);
		return true;
	}

	private static DatagramPacket packetFor(byte[] data, SocketAddress peerAddress) {
		if (peerAddress == null) return null;
		try {
			return new DatagramPacket(data, data.length, peerAddress); //Optimize: reuse DatagramPacket
		} catch (SocketException e) {
			my(ExceptionLogger.class).log(e);
			return null;
		}
	}

	private static byte[] ownSealBytes() {
		return my(OwnSeal.class).get().currentValue().bytes.copy();
	}

}
