package spikes.adenauer.puncher.server.impl;

import static spikes.adenauer.puncher.server.impl.BufferUtils.append;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class KeepAliveRequest {

	public static KeepAliveRequest umarshalFrom(DatagramPacket packet) {
		byte[] data = packet.getData();
		int pos = 0;
		
		List<byte[]> seals = new ArrayList<byte[]>();
		while (packet.getLength() > pos)
			seals.add(Arrays.copyOfRange(data, pos, pos += 64));
		
		byte[] ownSeal = seals.remove(0);
		
		return new KeepAliveRequest(ownSeal, seals);
	}

	
	private final byte[] _ownSeal;
	private final List<byte[]> _peersToFind;

	
	
	public List<byte[]> peersToFind() {
		return _peersToFind;
	}


	public KeepAliveRequest(byte[] seal, List<byte[]> peersToFind) {
		_ownSeal = seal;
		_peersToFind = peersToFind;
	}

	
	public int marshalTo(byte[] buf) {
		int pos = append(buf, 0, _ownSeal);

		for (byte[] peer : _peersToFind)
			pos = append(buf, pos, peer);

		return pos;
	}


	public byte[] seal() {
		return _ownSeal;
	}


}
