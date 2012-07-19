package sneer.bricks.network.computers.addresses.contacts.tcp;

import basis.brickness.Brick;
import sneer.bricks.network.computers.addresses.keeper.InternetAddress;
import sneer.bricks.pulp.reactive.collections.SetSignal;

@Brick
public interface ContactInternetAddresses {

	SetSignal<InternetAddress> addresses();

}