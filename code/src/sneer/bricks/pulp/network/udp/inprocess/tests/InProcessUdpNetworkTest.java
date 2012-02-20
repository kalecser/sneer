package sneer.bricks.pulp.network.udp.inprocess.tests;

import sneer.bricks.pulp.network.udp.UdpNetwork;
import sneer.bricks.pulp.network.udp.impl.tests.UdpNetworkTest;
import sneer.bricks.pulp.network.udp.inprocess.impl.InProcessUdpNetworkImpl;
import sneer.foundation.brickness.testsupport.Bind;




public class InProcessUdpNetworkTest extends UdpNetworkTest {

	@Bind UdpNetwork inProcess = new InProcessUdpNetworkImpl();
	
}
