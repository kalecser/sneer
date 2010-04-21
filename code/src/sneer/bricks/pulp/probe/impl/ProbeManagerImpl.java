package sneer.bricks.pulp.probe.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.HashMap;
import java.util.Map;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.computers.sockets.connections.ByteConnection;
import sneer.bricks.network.computers.sockets.connections.ConnectionManager;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.probe.ProbeManager;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.pulp.serialization.Serializer;
import sneer.foundation.lang.Consumer;

class ProbeManagerImpl implements ProbeManager {
	
	private static final Contacts ContactManager = my(Contacts.class);
	private static final ConnectionManager ConnectionManager = my(ConnectionManager.class);
	private static final Serializer Serializer = my(Serializer.class);
	private static final TupleSpace TupleSpace = my(TupleSpace.class);
	
	private static final ClassLoader CLASSLOADER_FOR_TUPLES = TupleSpace.class.getClassLoader();

	
	private final Map<Contact, ProbeImpl> _probesByContact = new HashMap<Contact, ProbeImpl>();
	@SuppressWarnings("unused") private final WeakContract _refToAvoidGc;

	
	{
		_refToAvoidGc = ContactManager.contacts().addReceiver(new Consumer<CollectionChange<Contact>>(){ @Override public void consume(CollectionChange<Contact> change) {
			for (Contact contact : change.elementsAdded()) startProbeFor(contact);
			for (Contact contact : change.elementsRemoved()) stopProbeFor(contact);
		}});
	}

	
	private void startProbeFor(Contact contact) {
		ByteConnection connection = ConnectionManager.connectionFor(contact);
		ProbeImpl probe = createProbe(contact, connection);
		connection.initCommunications(probe._scheduler, createReceiver(contact));
	}

	
	private void stopProbeFor(Contact contact) {
		ConnectionManager.closeConnectionFor(contact);
		_probesByContact.remove(contact);
	}
	

	private ProbeImpl createProbe(Contact contact, ByteConnection connection) {
		ProbeImpl result = new ProbeImpl(contact, connection.isConnected());
		_probesByContact.put(contact, result);
		return result;
	}

	
	private Consumer<byte[]> createReceiver(final Contact contact) {
		return new Consumer<byte[]>(){ @Override public void consume(byte[] packet) {
			final Object tuple = desserialize(packet, contact);
			if (tuple == null) return;
			TupleSpace.acquire((Tuple) tuple);
		}};
	}

	
	private Object desserialize(byte[] packet, Contact contact) {
		try {
			return Serializer.deserialize(packet, CLASSLOADER_FOR_TUPLES);
		} catch (ClassNotFoundException e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Unknown Tuple class received from " + contact, "Your peer might be running a brick version you don't have.", e, 30000);
			return null;
		}
	}

}