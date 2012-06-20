package sneer.bricks.network.computers.udp.holepuncher.server.impl;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.network.computers.udp.UdpNetwork;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunProtocol;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunReply;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunRequest;
import sneer.bricks.network.computers.udp.holepuncher.server.StunServer;


class StunServerImpl implements StunServer {

	private static final DatagramPacket[] EMPTY_ARRAY = new DatagramPacket[0];
	private final Map<String, IpAddresses> addressesBySeal = new HashMap<String, IpAddresses>();


	@Override
	public DatagramPacket[] repliesFor(DatagramPacket packet) {
		my(Logger.class).log("Stun server: Packet received ", packet);
		StunRequest req = my(StunProtocol.class).unmarshalRequest(ByteBuffer.wrap(packet.getData(), 0, packet.getLength()));
		if (req == null) return EMPTY_ARRAY;
		
		keepCallerAddresses(packet, req);

		byte[][] peers = req.peerSealsToFind;
		if (peers.length == 0) return EMPTY_ARRAY;
		
		IpAddresses callerAddr = addressesBySeal.get(toString(req.ownSeal));
		StunReply toPeer = new StunReply(req.ownSeal, callerAddr.publicInternetAddress, callerAddr.publicInternetPort, callerAddr.localAddressData);
		
		List<DatagramPacket> replies = new ArrayList<DatagramPacket>(1 + peers.length);
		for(byte[] peer : peers) {
			IpAddresses peerAddr = addressesBySeal.get(toString(peer));
			if(peerAddr == null) continue; 
			
			StunReply toCaller = new StunReply(peer, peerAddr.publicInternetAddress, peerAddr.publicInternetPort, peerAddr.localAddressData);
			
			prepareReply(replies, toCaller, callerAddr);
			prepareReply(replies, toPeer, peerAddr);
		}
		
		return replies.toArray(EMPTY_ARRAY);
	}


	private void prepareReply(List<DatagramPacket> replies, StunReply reply, IpAddresses addr) {
		DatagramPacket packet = packetTo(addr);
		marshal(reply, packet);
		replies.add(packet);
	}


	private DatagramPacket packetTo(IpAddresses addr) {
		DatagramPacket ret = new DatagramPacket(new byte[UdpNetwork.MAX_PACKET_PAYLOAD_SIZE], 0);
		ret.setAddress(addr.publicInternetAddress);
		ret.setPort(addr.publicInternetPort);
		return ret;
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


