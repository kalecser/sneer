package sneer.bricks.network.computers.sockets.connections.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.util.Arrays;

import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.sockets.connections.ContactSighting;
import sneer.bricks.network.computers.sockets.protocol.ProtocolTokens;
import sneer.bricks.pulp.events.EventNotifier;
import sneer.bricks.pulp.events.EventNotifiers;
import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.bricks.pulp.network.Network;

class IncomingHandShaker {

	
	private static final ContactSeals Seals = my(ContactSeals.class);
	private static EventNotifier<ContactSighting> contactSightings = my(EventNotifiers.class).newInstance();


	static Seal greet(ByteArraySocket socket) throws IOException {
		byte[] contactsSealBytes = identifyContactsSeal(socket);
		final Seal contactsSeal = new Seal(new ImmutableByteArray(contactsSealBytes));

		rejectLoopback(contactsSeal);
		rejectUnknownSeal(contactsSeal);
		//Implement: Challenge pk.

		notifySighting(socket, contactsSeal);
		
		return contactsSeal;
	}


	private static void notifySighting(ByteArraySocket socket, final Seal contactsSeal) {
		String ip = my(Network.class).remoteIpFor(socket);
		contactSightings.notifyReceivers(new ContactSightingImpl(contactsSeal, ip));
	}


	private static void rejectUnknownSeal(Seal contactsSeal) throws IOException {
		if (Seals.contactGiven(contactsSeal) == null)
			throw new IOException("Incoming connection from unknown party. Seal: " + contactsSeal);
	}


	static private void rejectLoopback(Seal peersSeal) throws IOException {
		if (peersSeal.equals(my(OwnSeal.class).get()))
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


	public static EventSource<ContactSighting> contactSightings() {
		return contactSightings.output();
	}

}
