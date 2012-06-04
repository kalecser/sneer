package sneer.bricks.network.computers.udp.holepuncher.server.impl;

import static basis.environments.Environments.my;
import static sneer.bricks.network.computers.udp.holepuncher.server.impl.DataUtils.dataInputFrom;
import static sneer.bricks.network.computers.udp.holepuncher.server.impl.DataUtils.ip;
import static sneer.bricks.network.computers.udp.holepuncher.server.impl.DataUtils.readNewArray;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import sneer.bricks.hardware.io.log.Logger;





public class StunRequest {

	public static StunRequest umarshalFrom(byte[] data, int length) {
		try {
			return tryToUnmarshalFrom(data, length);
		} catch (IOException e) {
			my(Logger.class).log("Exception unmarshalling StunRequest: " + e.getMessage());
			return null;
		}
	}


	private static StunRequest tryToUnmarshalFrom(byte[] data, int length) throws IOException {
		DataInputStream in = dataInputFrom(data, length);
		
		byte[] ownSeal = readNewArray(in, 64);
		InetAddress localIp = ip(readNewArray(in, 4));
		int localPort = in.readUnsignedShort();
		byte[] peerSeal = readNewArray(in, 64);

		return new StunRequest(ownSeal, localIp, localPort, peerSeal);
	}


	public final byte[] _ownSeal;
	public final byte[] _peerToFind;
	public final InetAddress _localIp;
	public final int _localPort;

	
	public StunRequest(byte[] seal, InetAddress localIp, int localPort, byte[] peerToFind) {
		_ownSeal = seal;
		_localIp = localIp;
		_localPort = localPort;
		_peerToFind = peerToFind;
	}

	
	public int marshalTo(byte[] buf) {
		ByteBuffer out = ByteBuffer.wrap(buf);
		
		out.put(_ownSeal);
		out.put(_localIp.getAddress());
		out.putChar((char)_localPort);
		
		if (_peerToFind != null)
			out.put(_peerToFind);
		
		return out.position();
	}

}
