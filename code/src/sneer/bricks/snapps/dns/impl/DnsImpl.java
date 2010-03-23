package sneer.bricks.snapps.dns.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.computers.sockets.connections.ConnectionManager;
import sneer.bricks.network.computers.sockets.connections.ContactSighting;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.snapps.dns.Dns;
import sneer.bricks.snapps.dns.DnsEntry;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Producer;

class DnsImpl implements Dns {

	private final ConnectionManager connectionManager = my(ConnectionManager.class);
	private final CacheMap<Seal, ListRegister<String>> addressesForContact = CacheMap.newInstance();
	
	@SuppressWarnings("unused")
	private final WeakContract _refToAvoidGc = connectionManager.contactSightings().addReceiver(new Consumer<ContactSighting>() { @Override public void consume(ContactSighting sighting) {
		sighted(sighting.seal(), sighting.ip());
	}});
	
	
	{
		my(TupleSpace.class).keep(DnsEntry.class);
	}
	
	
	@Override
	public ListSignal<String> knownIpsForContact(Seal seal) {
		return addressesForSeal(seal).output();
	}
	

	private void sighted(Seal seal, String ip) {
		addressesForSeal(seal).add(ip);
		my(TupleSpace.class).acquire(new DnsEntry(seal, ip));
	}

	
	private ListRegister<String> addressesForSeal(Seal seal) {
		return addressesForContact.get(seal, new Producer<ListRegister<String>>() {	@Override 	public ListRegister<String> produce() throws RuntimeException {
			return my(CollectionSignals.class).newListRegister();
		}});
	}

}
