package sneer.bricks.network.computers.addresses.keeper.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sneer.bricks.network.computers.addresses.keeper.InternetAddress;
import sneer.bricks.network.computers.addresses.keeper.InternetAddressKeeper;
import sneer.bricks.software.bricks.statestore.BrickStateStore;

abstract class Store {
	
	static List<Object[]> restore() {
		List<Object[]> addresses  = (List<Object[]>) my(BrickStateStore.class).readObjectFor(InternetAddressKeeper.class);
		return addresses != null
			? addresses
			: new ArrayList<Object[]>();
	}
	
	static void save(Collection<InternetAddress> currentAddresses) {
		List<Object[]> addresses = new ArrayList<Object[]>();
		for (InternetAddress address : currentAddresses) 
			addresses.add(new Object[]{
				address.contact().nickname().currentValue(),  
				address.host(), 
				address.port().currentValue()});

		my(BrickStateStore.class).writeObjectFor(InternetAddressKeeper.class, addresses);
	 }
}