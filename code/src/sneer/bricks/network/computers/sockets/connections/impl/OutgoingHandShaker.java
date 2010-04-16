package sneer.bricks.network.computers.sockets.connections.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;

import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.network.computers.sockets.protocol.ProtocolTokens;
import sneer.bricks.network.social.contacts.Contact;
import sneer.bricks.pulp.network.ByteArraySocket;

class OutgoingHandShaker {

	private static final OwnSeal OwnSeal = my(OwnSeal.class);

	
	static void greet(ByteArraySocket socket, @SuppressWarnings("unused") Contact contact) throws IOException {
		socket.write(ProtocolTokens.SNEER_WIRE_PROTOCOL_1);
		socket.write(OwnSeal.get().bytes.copy());
		
		//Implement: accept and pass pk challenge.
	}

}
