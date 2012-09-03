package sneer.bricks.pulp.probe.impl;

import static basis.environments.Environments.my;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.computers.channels.Channel;
import sneer.bricks.network.computers.channels.Channels;
import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.connections.ConnectionManager;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.probe.ProbeManager;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.pulp.serialization.Serializer;
import basis.lang.Consumer;

class ProbeManagerImpl implements ProbeManager {
	
	private static final Contacts ContactManager = my(Contacts.class);
	private static final ConnectionManager ConnectionManager = my(ConnectionManager.class);
	private static final Serializer Serializer = my(Serializer.class);
	private static final TupleSpace TupleSpace = my(TupleSpace.class);
	
	private final Map<Contact, ProbeImpl> _probesByContact = new HashMap<Contact, ProbeImpl>();
	@SuppressWarnings("unused") private final WeakContract _refToAvoidGc;

	
	{
		_refToAvoidGc = ContactManager.contacts().addReceiver(new Consumer<CollectionChange<Contact>>(){ @Override public void consume(CollectionChange<Contact> change) {
			for (Contact contact : change.elementsAdded()) startProbeFor(contact);
			for (Contact contact : change.elementsRemoved()) stopProbeFor(contact);
		}});
	}

	
	private void startProbeFor(Contact contact) {
		ProbeImpl probe;
		if (Channels.READY_FOR_PRODUCTION) {
			Channel ch = my(Channels.class).createControl(contact);
			probe = createProbe(contact, ch.isUp());
			ch.open(probe.packetProducer, createReceiver(contact));
		} else {
			ByteConnection connection = ConnectionManager.connectionFor(contact);
			probe = createProbe(contact, connection.isConnected());
			connection.initCommunications(probe.packetProducer, createReceiver(contact));
		}
	}

	
	private void stopProbeFor(Contact contact) {
		ConnectionManager.closeConnectionFor(contact);
		_probesByContact.remove(contact);
	}
	

	private ProbeImpl createProbe(Contact contact, Signal<Boolean> isConnected) {
		ProbeImpl result = new ProbeImpl(contact, isConnected);
		_probesByContact.put(contact, result);
		return result;
	}

	
	private Consumer<ByteBuffer> createReceiver(final Contact contact) {
		return new Consumer<ByteBuffer>(){ @Override public void consume(ByteBuffer packet) {
			final Object tuple = desserialize(packet, contact);
			if (tuple == null) return;
			TupleSpace.add((Tuple) tuple);
		}};
	}

	
	private Object desserialize(ByteBuffer packet, Contact contact) {
		try {
			byte[] bytes = new byte[packet.remaining()];
			packet.get(bytes);
			return Serializer.deserialize(bytes);
		} catch (Exception e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Error receiving tuple from " + contact, "Your peer might be running a brick version you don't have.", e, 30000);
			return null;
		}
	}

}