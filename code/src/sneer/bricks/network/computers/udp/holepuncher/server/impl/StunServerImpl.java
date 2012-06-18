package sneer.bricks.network.computers.udp.holepuncher.server.impl;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import sneer.bricks.network.computers.udp.UdpNetwork;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunProtocol;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunReply;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunRequest;
import sneer.bricks.network.computers.udp.holepuncher.server.StunServer;


class StunServerImpl implements StunServer {

	private static final DatagramPacket[] NO_PACKETS = new DatagramPacket[0];
	private static final DatagramPacket REPLY_TO_PEER = new DatagramPacket(new byte[UdpNetwork.MAX_PACKET_PAYLOAD_SIZE], 0);
	private final Map<String, IpAddresses> addressesBySeal = new HashMap<String, IpAddresses>();


	@Override
	public DatagramPacket[] repliesFor(DatagramPacket packet) {
		StunRequest req = my(StunProtocol.class).unmarshalRequest(packet.getData(), packet.getLength());
		if (req == null) return NO_PACKETS;
		
		keepCallerAddresses(packet, req);

		byte[][] peers = req.peerSealsToFind;
		if (peers.length == 0) return NO_PACKETS;

		IpAddresses peerAddr = addressesBySeal.get(toString(peers[0]));
		if (peerAddr == null) return NO_PACKETS;
		IpAddresses ownAddr = addressesBySeal.get(toString(req.ownSeal));
		StunReply toMe = new StunReply(peers[0], peerAddr.publicInternetAddress, peerAddr.publicInternetPort, peerAddr.localAddressData);
		StunReply toPeer = new StunReply(req.ownSeal, ownAddr.publicInternetAddress, ownAddr.publicInternetPort, ownAddr.localAddressData);
		
		marshal(toMe, packet);
		marshal(toPeer, REPLY_TO_PEER);
		return new DatagramPacket[]{packet, REPLY_TO_PEER};
	}


	private void marshal(StunReply reply, DatagramPacket packet) {
		int length = my(StunProtocol.class).marshalReplyTo(reply, packet.getData());
		packet.setLength(length);
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


