package sneer.bricks.network.computers.ips;

import sneer.bricks.network.computers.ips.keeper.InternetAddress;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import sneer.foundation.brickness.Brick;

@Brick
public interface ContactInternetAddresses {

	SetSignal<InternetAddress> addresses();

}