package sneer.bricks.network.computers.sockets.connections.impl;

import static basis.environments.Environments.my;

import java.io.IOException;

import sneer.bricks.identity.name.OwnName;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.network.computers.sockets.protocol.ProtocolTokens;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.network.ByteArraySocket;

class OutgoingHandShaker {

	private static final OwnSeal OwnSeal = my(OwnSeal.class);

	
	static void greet(ByteArraySocket socket, @SuppressWarnings("unused") Contact contact) throws IOException {
		socket.write(ProtocolTokens.SNEER_WIRE_PROTOCOL_1);
		socket.write(OwnSeal.get().currentValue().bytes.copy());
		socket.write(my(Attributes.class).myAttributeValue(OwnName.class).currentValue().getBytes("UTF-8"));
		
		//Implement: accept and pass pk challenge.
	}

}
