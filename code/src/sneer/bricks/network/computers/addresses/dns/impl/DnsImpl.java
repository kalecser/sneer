package sneer.bricks.network.computers.addresses.dns.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.computers.addresses.dns.Dns;
import sneer.bricks.network.computers.addresses.dns.DnsEntry;
import sneer.bricks.network.computers.sockets.connections.ConnectionManager;
import sneer.bricks.network.computers.sockets.connections.ContactSighting;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.lang.Consumer;

class DnsImpl implements Dns {

	private final ConnectionManager connectionManager = my(ConnectionManager.class);
	
	@SuppressWarnings("unused")
	private final WeakContract _refToAvoidGc = connectionManager.contactSightings().addReceiver(new Consumer<ContactSighting>() { @Override public void consume(ContactSighting sighting) {
		sighted(sighting.seal(), sighting.ip());
	}});
	
	
	{
		my(TupleSpace.class).keep(DnsEntry.class);
	}	

	private void sighted(Seal seal, String ip) {
		my(TupleSpace.class).acquire(new DnsEntry(seal, ip));
	}

	

}
