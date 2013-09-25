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

		IpAddresses callerAddr = addressesBySeal.get(toString(req.ownSeal));
		StunReply toAllPeers = new StunReply(req.ownSeal, callerAddr.publicInternetAddress, callerAddr.publicInternetPort, callerAddr.localAddressData);
		
		List<DatagramPacket> replies = new ArrayList<DatagramPacket>();
		for(byte[] peer : req.peerSealsToFind) {
			IpAddresses peerAddr = addressesBySeal.get(toString(peer));
			if (peerAddr == null) continue; 
			
			StunReply toCaller = new StunReply(peer, peerAddr.publicInternetAddress, peerAddr.publicInternetPort, peerAddr.localAddressData);
			
			prepareReply(replies, toCaller, callerAddr);
			prepareReply(replies, toAllPeers, peerAddr);
		}
		
		return replies.toArray(EMPTY_ARRAY);
	}


	private void prepareReply(List<DatagramPacket> replies, StunReply reply, IpAddresses addr) {
		DatagramPacket packet = packetTo(addr);
		marshal(reply, packet);
		replies.add(packet);
	}


	private DatagramPacket packetTo(IpAddresses addr) {
		return new DatagramPacket(
			new byte[UdpNetwork.MAX_PACKET_PAYLOAD_SIZE],
			0,
			addr.publicInternetAddress,
			addr.publicInternetPort
		);
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


	@Override
	public DatagramPacket[] repliesForAlternate(DatagramPacket packet) {
		return new DatagramPacket[]{packet}; //Simply echo for now.
	}

}


