package sneer.bricks.network.computers.ips.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.network.computers.ips.ContactInternetAddresses;
import sneer.bricks.network.computers.ips.keeper.InternetAddress;
import sneer.bricks.network.computers.ips.keeper.InternetAddressKeeper;
import sneer.bricks.pulp.reactive.collections.SetSignal;

class ContactInternetAddressesImpl implements ContactInternetAddresses {

	@Override
	public SetSignal<InternetAddress> addresses() {
		return my(InternetAddressKeeper.class).addresses();
	}

}
