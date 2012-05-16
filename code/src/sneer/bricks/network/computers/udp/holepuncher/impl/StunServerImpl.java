package sneer.bricks.network.computers.udp.holepuncher.impl;

import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import sneer.bricks.network.computers.udp.holepuncher.StunServer;


class StunServerImpl implements StunServer {

	private final Map<String, IpAddresses> addressesBySeal = new HashMap<String, IpAddresses>();


	@Override
	public DatagramPacket replyFor(DatagramPacket packet) {
		StunRequest req = StunRequest.umarshalFrom(packet.getData(), packet.getLength());
		if (req == null) return null;
		
		keepCallerAddresses(packet, req);

		byte[] peer = req._peerToFind;
		if (peer == null) return null;

		IpAddresses addr = addressesBySeal.get(toString(peer));
		StunReply reply = new StunReply(peer, addr.publicInternetAddress, addr.publicInternetPort, addr.localNetworkAddress, addr.localNetworkPort);
		
		int length = reply.marshalTo(packet.getData());
		packet.setLength(length);
		return packet;
	}


	private void keepCallerAddresses(DatagramPacket packet, StunRequest req) {
		String caller = toString(req._ownSeal);
		IpAddresses addresses = new IpAddresses(packet.getAddress(), packet.getPort(), req._localIp, req._localPort);
		addressesBySeal.put(caller, addresses);
	}

	
	static private String toString(byte[] arr) {
		return Arrays.toString(arr);
	}
}


