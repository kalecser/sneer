package spikes.adenauer.puncher.server.impl;

import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spikes.adenauer.puncher.IpAddresses;
import spikes.adenauer.puncher.server.StunServer;


class StunServerImpl implements StunServer {

	private final Map<String, IpAddresses> addressesBySeal = new HashMap<String, IpAddresses>();


	@Override
	public DatagramPacket replyFor(DatagramPacket packet) {
		KeepAliveRequest req = KeepAliveRequest.umarshalFrom(packet);
		keepCallerAddresses(packet, req);

		List<byte[]> peers = req.peersToFind();
		if (peers.isEmpty()) return null;

		byte[] peer = peers.remove(0);
		IpAddresses addr = addressesBySeal.get(toString(peer));
		KeepAliveReply reply = new KeepAliveReply(peer, addr.publicInternetAddress, addr.publicInternetPort);
		
		reply.marshalTo(packet);
		return packet;
	}


	private void keepCallerAddresses(DatagramPacket packet, KeepAliveRequest req) {
		String caller = toString(req.seal());
		IpAddresses addresses = new IpAddresses(packet.getAddress(), packet.getPort(), null, -1);
		addressesBySeal.put(caller, addresses);
	}

	
	static private String toString(byte[] arr) {
		return Arrays.toString(arr);
	}
}


