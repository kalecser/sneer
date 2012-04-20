package sneer.bricks.network.computers.sockets.connections.impl;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.util.Arrays;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.connections.Call;
import sneer.bricks.network.computers.sockets.connections.Sighting;
import sneer.bricks.network.computers.sockets.protocol.ProtocolTokens;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.bricks.pulp.network.Network2010;
import sneer.bricks.pulp.notifiers.Notifier;
import sneer.bricks.pulp.notifiers.Notifiers;
import sneer.bricks.pulp.notifiers.Source;
import basis.lang.arrays.ImmutableByteArray;

class IncomingHandShaker {

	
	private static final ContactSeals Seals = my(ContactSeals.class);

	private static Notifier<Call> unknownCallers = my(Notifiers.class).newInstance();

	static Seal greet(ByteArraySocket socket) throws IOException {
		Seal contactsSeal = readContactsSeal(socket);
		String contactsName = readContactsName(socket);

		rejectLoopback(contactsSeal);
		rejectUnknownSeal(contactsName, contactsSeal);

//		my(Authenticator.class).authenticate(contactsSeal, socket);
//		private static void authenticate(final Seal contactsSeal, ByteArraySocket socket) throws IOException {
//			if (!my(PublicKeyChallenges.class).challenge(contactsSeal, socket))
//				throw new IOException("Incoming connection failed to authenticate.");
//		}

		notifySighting(contactsSeal, socket);
		
		return contactsSeal;
	}






	private static void notifySighting(final Seal contactsSeal, ByteArraySocket socket) {
		String ip = my(Network2010.class).remoteIpFor(socket);
		my(TupleSpace.class).add(new Sighting(contactsSeal, ip));
	}


	private static void rejectUnknownSeal(String contactsName, Seal contactsSeal) throws IOException {
		if (Seals.contactGiven(contactsSeal) != null) return;
		
		unknownCallers.notifyReceivers(new CallImpl(contactsName, contactsSeal));
		throw new IOException("Incoming connection from unknown party. Seal: " + contactsSeal);
	}


	static private void rejectLoopback(Seal peersSeal) throws IOException {
		if (peersSeal.equals(my(OwnSeal.class).get().currentValue()))
			throw new IOException("Socket identified as originating from yourself.");
	}


	private static Seal readContactsSeal(ByteArraySocket socket) throws IOException {
		byte[] result = readContactsSealBytes(socket);
		return new Seal(new ImmutableByteArray(result));
	}


	private static String readContactsName(ByteArraySocket socket) throws IOException {
		byte[] result = socket.read();
		return new String(result, "UTF-8");
	}


	static private byte[] readContactsSealBytes(ByteArraySocket socket) throws IOException {
		while (true) {
			byte[] header = socket.read();
			byte[] sealBytes = socket.read();
			
			if (Arrays.equals(header, ProtocolTokens.SNEER_WIRE_PROTOCOL_1))
				return sealBytes;
			
			socket.write(ProtocolTokens.FALLBACK);
		}
	}


	static Source<Call> unknownCallers() {
		return unknownCallers.output();
	}

}
