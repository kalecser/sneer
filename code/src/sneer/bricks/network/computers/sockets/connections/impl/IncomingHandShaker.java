package sneer.bricks.network.computers.sockets.connections.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.util.Arrays;

import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.network.computers.sockets.protocol.ProtocolTokens;
import sneer.bricks.pulp.keymanager.Seal;
import sneer.bricks.pulp.keymanager.ContactSeals;
import sneer.bricks.pulp.network.ByteArraySocket;

class IncomingHandShaker {

	
	private static final ContactSeals Seals = my(ContactSeals.class);


	static Seal greet(ByteArraySocket socket) throws IOException {
		byte[] contactsSealBytes = identifyContactsSeal(socket);
		Seal contactsSeal = new Seal(new ImmutableByteArray(contactsSealBytes));

		rejectLoopback(contactsSeal);
		rejectUnknownSeal(contactsSeal);
		//Implement: Challenge pk.

		return contactsSeal;
	}


	private static void rejectUnknownSeal(Seal contactsSeal) throws IOException {
		if (Seals.contactGiven(contactsSeal) == null)
			throw new IOException("Incoming connection from unknown party. Seal: " + contactsSeal);
	}


	static private void rejectLoopback(Seal peersSeal) throws IOException {
		if (peersSeal.equals(Seals.ownSeal()))
			throw new IOException("Socket identified as originating from yourself.");
	}


	static private byte[] identifyContactsSeal(ByteArraySocket socket) throws IOException {
		while (true) {
			byte[] header = socket.read();
			byte[] sealBytes = socket.read();
			
			if (Arrays.equals(header, ProtocolTokens.SNEER_WIRE_PROTOCOL_1))
				return sealBytes;
			
			socket.write(ProtocolTokens.FALLBACK);
		}
	}

}
