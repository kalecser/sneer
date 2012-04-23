package sneer.bricks.network.computers.udp.puncher.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.StringTokenizer;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.computers.udp.puncher.Puncher;

public class PuncherImpl implements Puncher {
	
	
	@Override
	public InetSocketAddress rendezvous(Seal seal) throws IOException {
		return addressOf(seal);
	}

	
	private InetSocketAddress addressOf(Seal seal) throws IOException {
		StringTokenizer targetInfo = requestInfoOf(seal);
		InetSocketAddress privateAddress = decode(targetInfo);
		InetSocketAddress publicAddress = decode(targetInfo);
		if (privateAddress.getAddress().isSiteLocalAddress()) return privateAddress; //Peer is in same network. 
		return publicAddress;
	}

	
	private StringTokenizer requestInfoOf(Seal seal) throws IOException {
		String requestInfo = request(encode(seal));
		StringTokenizer fields = new StringTokenizer(requestInfo, ";");
		return fields;
	}

	
	private String request(DatagramPacket packet) throws IOException {
		DatagramSocket socket = new DatagramSocket();
		socket.send(packet);
		socket.receive(packet);
		String requestInto = new String(packet.getData()); 
		return requestInto;
	}
	

	private InetSocketAddress decode(StringTokenizer targetInfo) {
		String ip = targetInfo.nextToken();
		int port = Integer.valueOf(targetInfo.nextToken());
		return new InetSocketAddress(ip, port);
	}


	private DatagramPacket encode(Seal seal) {
		byte[] data = seal.bytes.copy();
		DatagramPacket requestPacket = new DatagramPacket(data, data.length);
		requestPacket.setSocketAddress(rendezvousAddress());
		return requestPacket;
	}

	
	private  InetSocketAddress rendezvousAddress() {
		InetSocketAddress address = new InetSocketAddress("wuestefeld.name", 9876);
		return address;
	}

}