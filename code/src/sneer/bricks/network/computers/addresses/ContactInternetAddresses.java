package sneer.bricks.network.computers.addresses;

import sneer.bricks.network.computers.addresses.keeper.InternetAddress;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import sneer.foundation.brickness.Brick;

@Brick
public interface ContactInternetAddresses {

	SetSignal<InternetAddress> addresses();

}