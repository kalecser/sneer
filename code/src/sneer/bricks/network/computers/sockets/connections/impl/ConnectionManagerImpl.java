package sneer.bricks.network.computers.sockets.connections.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.computers.sockets.connections.ConnectionManager;
import sneer.bricks.network.computers.sockets.connections.ContactSighting;
import sneer.bricks.network.social.contacts.Contact;
import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.foundation.lang.ClosureX;

class ConnectionManagerImpl implements ConnectionManager {
	
	static final WeakContract crashingContract = my(Threads.class).crashing().addPulseReceiver(new Runnable() { @Override public void run() {
		for (ByteConnectionImpl victim : ConnectionsByContact.all())
			victim.close();
	}});

	
	@Override
	public ByteConnectionImpl connectionFor(final Contact contact) {
		return ConnectionsByContact.get(contact);
	}

	
	@Override
	public void manageIncomingSocket(final ByteArraySocket socket) {
		manageSocket(socket, "Incoming", new ClosureX<IOException>() { @Override public void run() throws IOException {
			Seal contactsSeal = IncomingHandShaker.greet(socket);
			TieBreaker.manageIncomingSocket(socket, contactsSeal);
		}});
	}


	@Override
	public void manageOutgoingSocket(final ByteArraySocket socket, final Contact contact) {
		manageSocket(socket, "Outgoing", new ClosureX<IOException>() { @Override public void run() throws IOException {
			OutgoingHandShaker.greet(socket, contact);
			TieBreaker.manageOutgoingSocket(socket, contact);
		}});
	}

	
	private void manageSocket(final ByteArraySocket socket, String direction, ClosureX<IOException> closure) {
		SocketCloser.closeIfUnsuccessful(socket, direction + " socket closed.", closure);
		
		if (my(Threads.class).isCrashing())
			SocketCloser.close(socket, "Closing socket that was " + direction + " while crashing all threads.");
	}

	
	@Override
	public void closeConnectionFor(Contact contact) {
		ByteConnectionImpl connection = ConnectionsByContact.remove(contact);
		if (connection != null) connection.close();
	}


	@Override
	public EventSource<ContactSighting> contactSightings() {
		return IncomingHandShaker.contactSightings();
	}

}