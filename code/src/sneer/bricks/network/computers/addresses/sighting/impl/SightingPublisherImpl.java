package sneer.bricks.network.computers.addresses.sighting.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.computers.addresses.sighting.Sighting;
import sneer.bricks.network.computers.addresses.sighting.SightingPublisher;
import sneer.bricks.network.computers.sockets.connections.ConnectionManager;
import sneer.bricks.network.computers.sockets.connections.ContactSighting;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.lang.Consumer;

class SightingPublisherImpl implements SightingPublisher {

	private final ConnectionManager connectionManager = my(ConnectionManager.class);
	
	@SuppressWarnings("unused")
	private final WeakContract _refToAvoidGc = connectionManager.contactSightings().addReceiver(new Consumer<ContactSighting>() { @Override public void consume(ContactSighting sighting) {
		sighted(sighting.seal(), sighting.ip());
	}});
	
	
	{
		my(TupleSpace.class).keep(Sighting.class);
	}	

	private void sighted(Seal seal, String ip) {
		my(TupleSpace.class).acquire(new Sighting(seal, ip));
	}

	

}
