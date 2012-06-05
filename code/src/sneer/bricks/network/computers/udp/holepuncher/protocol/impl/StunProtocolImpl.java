package sneer.bricks.network.computers.udp.holepuncher.protocol.impl;

import static sneer.bricks.network.computers.udp.holepuncher.protocol.impl.DataUtils.ip;
import static sneer.bricks.network.computers.udp.holepuncher.protocol.impl.DataUtils.getNextArray;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import basis.lang.exceptions.NotImplementedYet;

import sneer.bricks.network.computers.udp.holepuncher.protocol.StunProtocol;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunReply;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunRequest;

class StunProtocolImpl implements StunProtocol {

	
	@Override
	public int marshalRequestTo(StunRequest request, byte[] data) {
//		ByteBuffer buf = ByteBuffer.wrap(data);
//		
//		buf.put(request._ownSeal);
//		
//		InetAddress[] ips = request._localAddressData;
//		
//		buf.put((byte)ips.length);
//		for (int i = 0; i < ips.length; i++)
//			buf.put(ips[i].getAddress());
//				
//		buf.putChar((char)request._localPort);
//		
//		if (request._peerToFind != null)
//			buf.put(request._peerToFind);
//		
//		return buf.position();
		throw new NotImplementedYet();
	}


	@Override
	public StunRequest unmarshalRequest(byte[] data, int length) {
		ByteBuffer buf = ByteBuffer.wrap(data, 0, length);
		
		byte[] ownSeal = getNextArray(buf, 64);
		
		byte ipsLength = buf.get();
		InetAddress[] localIps = new InetAddress[ipsLength];
		for (int i = 0; i < ipsLength; i++)
			localIps[i] = ip(getNextArray(buf, 4));
		
		int localPort = buf.getChar();
		byte[] peerSeal = getNextArray(buf, 64);

		//return new StunRequest(ownSeal, localIps, localPort, peerSeal);
		throw new NotImplementedYet();
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

		byte[] peerSeal = getNextArray(buf, 64);
		InetAddress ip = ip(getNextArray(buf, 4));
		int port = buf.getChar();
		InetAddress localIp = ip(getNextArray(buf, 4));
		int localPort = buf.getChar();
		
		return new StunReply(peerSeal, ip, port, localIp, localPort);
	}

}
