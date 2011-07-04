package sneer.bricks.pulp.network.udp.puncher;

import java.io.IOException;
import java.net.InetSocketAddress;

import sneer.bricks.identity.seals.Seal;
import sneer.foundation.brickness.Brick;

@Brick
public interface Puncher {
	InetSocketAddress rendezvous(Seal seal) throws IOException;
}
