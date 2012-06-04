package sneer.bricks.network.computers.udp.holepuncher.protocol.impl;

import static sneer.bricks.network.computers.udp.holepuncher.protocol.impl.DataUtils.ip;
import static sneer.bricks.network.computers.udp.holepuncher.protocol.impl.DataUtils.readNextArray;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import sneer.bricks.network.computers.udp.holepuncher.protocol.StunProtocol;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunReply;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunRequest;

class StunProtocolImpl implements StunProtocol {

	
	@Override
	public int marshalRequestTo(StunRequest request, byte[] data) {
		ByteBuffer buf = ByteBuffer.wrap(data);
		
		buf.put(request._ownSeal);
		buf.put(request._localIp.getAddress());
		buf.putChar((char)request._localPort);
		
		if (request._peerToFind != null)
			buf.put(request._peerToFind);
		
		return buf.position();
	}


	@Override
	public StunRequest unmarshalRequest(byte[] data, int length) {
		ByteBuffer buf = ByteBuffer.wrap(data, 0, length);
		
		byte[] ownSeal = readNextArray(buf, 64);
		InetAddress localIp = ip(readNextArray(buf, 4));
		int localPort = buf.getChar();
		byte[] peerSeal = readNextArray(buf, 64);

		return new StunRequest(ownSeal, localIp, localPort, peerSeal);
	}

	
	@Override
	public int marshalReplyTo(StunReply reply, byte[] data) {
		ByteBuffer buf = ByteBuffer.wrap(data);
		
		buf.put(reply.peerSeal);
		buf.put(reply.peerIp.getAddress());
		buf.putChar((char)reply.peerPort);
		buf.put(reply.peerLocalIp.getAddress());
		buf.putChar((char)reply.peerLocalPort);
		
		return buf.position();
	}


	@Override
	public StunReply unmarshalReply(byte[] data, int length) {
		ByteBuffer buf = ByteBuffer.wrap(data, 0, length);

		byte[] peerSeal = readNextArray(buf, 64);
		InetAddress ip = ip(readNextArray(buf, 4));
		int port = buf.getChar();
		InetAddress localIp = ip(readNextArray(buf, 4));
		int localPort = buf.getChar();
		
		return new StunReply(peerSeal, ip, port, localIp, localPort);
	}

}
