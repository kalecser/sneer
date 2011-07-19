package spikes.adenauer.network.udp;

import java.net.SocketAddress;

import sneer.bricks.identity.seals.Seal;


public interface UdpAddressResolver {

	SocketAddress addressFor(Seal seal);

}
