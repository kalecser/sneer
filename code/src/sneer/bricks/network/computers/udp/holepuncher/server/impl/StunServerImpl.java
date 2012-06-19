package sneer.bricks.network.computers.udp.holepuncher.server.impl;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.network.computers.udp.UdpNetwork;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunProtocol;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunReply;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunRequest;
import sneer.bricks.network.computers.udp.holepuncher.server.StunServer;


class StunServerImpl implements StunServer {

	private static final DatagramPacket[] NO_PACKETS = new DatagramPacket[0];
	private static final DatagramPacket PACKET_TO_PEER = new DatagramPacket(new byte[UdpNetwork.MAX_PACKET_PAYLOAD_SIZE], 0);
	private final Map<String, IpAddresses> addressesBySeal = new HashMap<String, IpAddresses>();


	@Override
	public DatagramPacket[] repliesFor(DatagramPacket packet) {
		my(Logger.class).log("Stun server: Packet received ", packet);
		StunRequest req = my(StunProtocol.class).unmarshalRequest(ByteBuffer.wrap(packet.getData(), 0, packet.getLength()));
		if (req == null) return NO_PACKETS;
		
		keepCallerAddresses(packet, req);

		byte[][] peers = req.peerSealsToFind;
		if (peers.length == 0) return NO_PACKETS;

		IpAddresses peerAddr = addressesBySeal.get(toString(peers[0]));
		if (peerAddr == null) return NO_PACKETS;
		IpAddresses ownAddr = addressesBySeal.get(toString(req.ownSeal));
		StunReply toCaller = new StunReply(peers[0], peerAddr.publicInternetAddress, peerAddr.publicInternetPort, peerAddr.localAddressData);
		StunReply toPeer = new StunReply(req.ownSeal, ownAddr.publicInternetAddress, ownAddr.publicInternetPort, ownAddr.localAddressData);
		
		marshal(toCaller, packet);
		marshal(toPeer, PACKET_TO_PEER);
		return new DatagramPacket[]{packet, PACKET_TO_PEER};
	}


	private void marshal(StunReply reply, DatagramPacket packet) {
		ByteBuffer buf = ByteBuffer.wrap(packet.getData());
		my(StunProtocol.class).marshalReplyTo(reply, buf);
		packet.setLength(buf.position());
	}


	private void keepCallerAddresses(DatagramPacket packet, StunRequest req) {
		String caller = toString(req.ownSeal);
		IpAddresses addresses = new IpAddresses(packet.getAddress(), packet.getPort(), req.localAddressData);
		addressesBySeal.put(caller, addresses);
	}

	
	static private String toString(byte[] arr) {
		return Arrays.toString(arr);
	}

}


