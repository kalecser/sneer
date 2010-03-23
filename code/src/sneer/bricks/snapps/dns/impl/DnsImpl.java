package sneer.bricks.snapps.dns.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.LinkedHashMap;
import java.util.Map;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.computers.sockets.connections.ConnectionManager;
import sneer.bricks.network.computers.sockets.connections.ContactSighting;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.snapps.dns.Dns;
import sneer.bricks.snapps.dns.DnsEntry;
import sneer.foundation.lang.Consumer;

public class DnsImpl implements Dns {

	ConnectionManager connectionManager = my(ConnectionManager.class);
	
	final Map<Seal, ListRegister<String>> addressesForContact = new LinkedHashMap<Seal, ListRegister<String>>();
	
	public DnsImpl(){
		registerToSightings();
		my(TupleSpace.class).keep(DnsEntry.class);
	}

	@Override
	public synchronized ListSignal<String> knownIpsForContact(Seal seal) {
		return addressesForSeal(seal).output();
	}
	
	private void registerToSightings() {
		connectionManager.contactSightings().addReceiver(new Consumer<ContactSighting>() {
			@Override
			public void consume(ContactSighting sighting) {
				System.out.println(Thread.currentThread());
				sighted(sighting.seal(), sighting.ip());
			}
		});
	}
	
	protected synchronized void sighted(Seal seal, String ip) {
		addressesForSeal(seal).add(ip);
		my(TupleSpace.class).acquire(new DnsEntry(seal, ip));
	}

	private synchronized ListRegister<String> addressesForSeal(Seal seal) {
		if (!addressesForContact.containsKey(seal)){
			ListRegister<String> ips = my(CollectionSignals.class).newListRegister();
			addressesForContact.put(seal, ips);
		}
		
		return addressesForContact.get(seal);
	}

}
