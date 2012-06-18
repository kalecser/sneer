package sneer.bricks.network.computers.udp.holepuncher.protocol.impl;

import static basis.environments.Environments.my;
import static sneer.bricks.network.computers.udp.holepuncher.protocol.impl.DataUtils.getNextArray;
import static sneer.bricks.network.computers.udp.holepuncher.protocol.impl.DataUtils.ip;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import sneer.bricks.network.computers.udp.holepuncher.protocol.StunProtocol;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunReply;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunRequest;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;

class StunProtocolImpl implements StunProtocol {
	
	private static final SocketAddress SERVER_ADDRESS = initServerAddress();
	private static final String SERVER_HOST_NAME = "dynamic.sneer.me";
	private static final int SERVER_PORT = 7777;
	
	@Override
	public int marshalRequestTo(StunRequest request, byte[] data) {
		ByteBuffer buf = ByteBuffer.wrap(data);
		
		buf.put(request.ownSeal);
		
		buf.put((byte)request.peerSealsToFind.length);
		for (byte[] seal : request.peerSealsToFind)
			buf.put(seal);
		
		buf.put(request.localAddressData);
		
		return buf.position();
	}


	@Override
	public StunRequest unmarshalRequest(byte[] data, int length) {
		ByteBuffer buf = ByteBuffer.wrap(data, 0, length);
		
		byte[] ownSeal = getNextArray(buf, 64);
		
		byte sealCount = buf.get();
		byte[][] peerSealsToFind = new byte[sealCount][]; 
		for (int i = 0; i < sealCount; i++) 
			peerSealsToFind[i] = getNextArray(buf, 64);		
				
		byte[] localAddressData = getNextArray(buf, buf.remaining());
		return new StunRequest(ownSeal, peerSealsToFind, localAddressData );
	}

	
	@Override
	public int marshalReplyTo(StunReply reply, byte[] data) {
		ByteBuffer buf = ByteBuffer.wrap(data);
		
		buf.put(reply.peerSeal);
		buf.put(reply.peerIp.getAddress());
		buf.putChar((char)reply.peerPort);
		buf.put(reply.peerLocalAddressData);
		
		return buf.position();
	}


	@Override
	public StunReply unmarshalReply(byte[] data, int length) {
		ByteBuffer buf = ByteBuffer.wrap(data, 0, length);

		byte[] peerSeal = getNextArray(buf, 64);
		InetAddress ip = ip(getNextArray(buf, 4));
		int port = buf.getChar();
		byte[] localAddressData = getNextArray(buf, buf.remaining());
		
		return new StunReply(peerSeal, ip, port, localAddressData);
	}
	

	@Override
	public SocketAddress serverAddress() {
		return SERVER_ADDRESS;
	}


	static private SocketAddress initServerAddress() {		
		InetAddress host = serverHost();
		return host == null ? null : new InetSocketAddress(host, SERVER_PORT);
	}


	private static InetAddress serverHost() {
		try {
			return "true".equals(System.getProperty("sneer.testmode"))
				? InetAddress.getByAddress(SERVER_HOST_NAME, new byte[]{111,112,113,114}) //Avoid dns lookup
				: InetAddress.getByName(SERVER_HOST_NAME);
		} catch (UnknownHostException e) {
			my(BlinkingLights.class).turnOn(LightType.WARNING, "Stun Server not found", "Unable to resolve DNS for " + SERVER_HOST_NAME, 15000);
			return null;
		}
	}

}
