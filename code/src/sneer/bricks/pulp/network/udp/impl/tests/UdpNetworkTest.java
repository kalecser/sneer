package sneer.bricks.pulp.network.udp.impl.tests;

import org.junit.Ignore;

import sneer.bricks.pulp.network.Network2010;
import sneer.bricks.pulp.network.tests.Network2010Test;
import sneer.bricks.pulp.network.udp.impl.UdpNetworkImpl;
import sneer.foundation.brickness.testsupport.Bind;

@Ignore
public class UdpNetworkTest extends Network2010Test {
	
	@Bind Network2010 subject = new UdpNetworkImpl();

}
