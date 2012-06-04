package sneer.bricks.network.computers.udp.holepuncher.server.impl;

import static sneer.bricks.network.computers.udp.holepuncher.server.impl.DataUtils.dataInputFrom;
import static sneer.bricks.network.computers.udp.holepuncher.server.impl.DataUtils.ip;
import static sneer.bricks.network.computers.udp.holepuncher.server.impl.DataUtils.readNewArray;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;


public class StunReply {

	public static StunReply unmarshalFrom(byte[] data, int length) throws IOException {
		DataInputStream in = dataInputFrom(data, length);

		byte[] peerSeal = readNewArray(in, 64);
		InetAddress ip = ip(readNewArray(in, 4));
		int port = in.readUnsignedShort();
		InetAddress localIp = ip(readNewArray(in, 4));
		int localPort = in.readUnsignedShort();
		
		return new StunReply(peerSeal, ip, port, localIp, localPort);
	}


	public final byte[] peerSeal;
	public final InetAddress peerIp;
	public final int peerPort;
	public final InetAddress peerLocalIp;
	public final int peerLocalPort;

	
	
	public StunReply(byte[] peerSeal_, InetAddress peerIp_, int peerPort_, InetAddress peerLocalIp_, int peerLocalPort_) {
		peerSeal = peerSeal_;
		peerIp = peerIp_;
		peerPort = peerPort_;
		peerLocalIp = peerLocalIp_;
		peerLocalPort = peerLocalPort_;
	}
	
	
	public int marshalTo(byte[] buf) {
		ByteBuffer data = ByteBuffer.wrap(buf);
		
		data.put(peerSeal);
		data.put(peerIp.getAddress());
		data.putChar((char)peerPort);
		data.put(peerLocalIp.getAddress());
		data.putChar((char)peerLocalPort);
		
		return data.position();
	}

}