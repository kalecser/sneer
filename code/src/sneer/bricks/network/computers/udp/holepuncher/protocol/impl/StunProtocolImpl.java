package sneer.bricks.network.computers.udp.holepuncher.protocol.impl;

import static basis.environments.Environments.my;
import static sneer.bricks.network.computers.udp.holepuncher.protocol.impl.DataUtils.getNextArray;
import static sneer.bricks.network.computers.udp.holepuncher.protocol.impl.DataUtils.ip;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.network.computers.udp.connections.UdpPacketType;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunProtocol;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunReply;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunRequest;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;

class StunProtocolImpl implements StunProtocol {
	
	private static final String SERVER_HOST_NAME = "dynamic.sneer.me";
	private static final int SERVER_PORT = 5555;
	
	@Override
	public void marshalRequestTo(StunRequest request, ByteBuffer out) {
		out.put(request.ownSeal);
		
		out.put((byte)request.peerSealsToFind.length);
		for (byte[] seal : request.peerSealsToFind)
			out.put(seal);
		
		out.put(request.localAddressData);
	}


	@Override
	public StunRequest unmarshalRequest(ByteBuffer in) {
		try {
			return tryToUnmarshalRequest(in);
		} catch (RuntimeException e) {
			my(Logger.class).log("Stun server: {} {}", e.getClass(), e.getLocalizedMessage());
			return null;
		}
	}


	private StunRequest tryToUnmarshalRequest(ByteBuffer in) {
		byte[] ownSeal = getNextArray(in, 64);
		if (ownSeal == null) return null;
		
		byte peerCount = in.get();
		byte[][] peerSealsToFind = new byte[peerCount][]; 
		for (int i = 0; i < peerCount; i++) 
			peerSealsToFind[i] = getNextArray(in, 64);		
				
		byte[] localAddressData = getNextArray(in, in.remaining());
		return new StunRequest(ownSeal, peerSealsToFind, localAddressData );
	}

	
	@Override
	public void marshalReplyTo(StunReply reply, ByteBuffer out) {
		out.put((byte)UdpPacketType.Stun.ordinal());
		out.put(reply.peerSeal);
		out.put(reply.peerIp.getAddress());
		out.putChar((char)reply.peerPort);
		out.put(reply.peerLocalAddressData);
	}


	@Override
	public StunReply unmarshalReply(ByteBuffer in) {
		byte[] peerSeal = getNextArray(in, 64);
		InetAddress ip = ip(getNextArray(in, 4));
		int port = in.getChar();
		byte[] localAddressData = getNextArray(in, in.remaining());
		
		return new StunReply(peerSeal, ip, port, localAddressData);
	}
	
	
	@Override
	public InetAddress serverHost() {
		try {
			return "true".equals(System.getProperty("sneer.testmode"))
				? InetAddress.getByAddress(SERVER_HOST_NAME, new byte[]{127,0,0,1})
				: InetAddress.getByName(SERVER_HOST_NAME);
		} catch (UnknownHostException e) {
			my(BlinkingLights.class).turnOn(LightType.WARNING, "Stun Server not found", "Unable to resolve DNS for " + SERVER_HOST_NAME + ". This might make it harder for Sneer to find your peers.", 15000);
			return null;
		}
	}


	@Override
	public int serverPort() {
		return SERVER_PORT;
	}

}
