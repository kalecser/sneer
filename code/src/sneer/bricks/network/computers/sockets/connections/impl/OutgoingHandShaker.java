package sneer.bricks.network.computers.sockets.connections.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;

import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.sockets.protocol.ProtocolTokens;
import sneer.bricks.pulp.network.ByteArraySocket;

class OutgoingHandShaker {

	private static final ContactSeals Seals = my(ContactSeals.class);

	
	static void greet(ByteArraySocket socket) throws IOException {
		socket.write(ProtocolTokens.SNEER_WIRE_PROTOCOL_1);
		socket.write(Seals.ownSeal().bytes.copy());
		
		//Implement: accept and pass pk challenge.
	}

}
