package sneer.bricks.network.computers.addresses.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.network.computers.addresses.ContactInternetAddresses;
import sneer.bricks.network.computers.addresses.keeper.InternetAddress;
import sneer.bricks.network.computers.addresses.keeper.InternetAddressKeeper;
import sneer.bricks.pulp.reactive.collections.SetSignal;

class ContactInternetAddressesImpl implements ContactInternetAddresses {

	@Override
	public SetSignal<InternetAddress> addresses() {
		return my(InternetAddressKeeper.class).addresses();
	}

}
