package spikes.adenauer.puncher.server.impl;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;


public class KeepAliveReply {

	public static KeepAliveReply unmarshalFrom(DatagramPacket packet) {
		byte[] data = packet.getData();
		int pos = 0;

		byte[] peerSeal = Arrays.copyOfRange(data, pos, pos += 64);

		InetAddress ip = ipGiven(Arrays.copyOfRange(data, pos, pos += 4));

		int port = data[pos++];
		port = port << 8;
		port |= data[pos];

		return new KeepAliveReply(peerSeal, ip, port);
	}


	private final byte[] peerSeal;
	private final InetAddress peerIp;
	private final int peerPort;

	
	public KeepAliveReply(byte[] peerSeal_, InetAddress peerIp_, int peerPort_) {
		peerSeal = peerSeal_;
		peerIp = peerIp_;
		peerPort = peerPort_;
	}
	
	
	private static InetAddress ipGiven(byte[] bytes) {
		try {
			return InetAddress.getByAddress(bytes);
		} catch (UnknownHostException e) {
			throw new IllegalStateException(e);
		}
	}
	
	
	public byte[] peerSeal() {
		return peerSeal;
	}

	
	public InetAddress peerIp() {
		return peerIp;
	}

	
	public int peerPort() {
		return peerPort;
	}


	public void marshalTo(DatagramPacket packet) {
		byte[] buf = packet.getData();
		int pos = 0;

		pos = BufferUtils.append(buf, pos, peerSeal);
		pos = BufferUtils.append(buf, pos, peerIp.getAddress());
		
		buf[pos++] = (byte)(peerPort >>> 8);
		buf[pos++] = (byte)(peerPort & 0xFF);
		
		packet.setLength(pos);
	}



}
