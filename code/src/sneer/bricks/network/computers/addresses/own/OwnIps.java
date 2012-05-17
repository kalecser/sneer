package sneer.bricks.network.computers.addresses.own;

import java.net.InetAddress;

import sneer.bricks.pulp.reactive.collections.SetSignal;
import basis.brickness.Brick;

@Brick
public interface OwnIps {

	SetSignal<InetAddress> get();
	
}
