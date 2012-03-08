package sneer.bricks.pulp.network.udp.inprocess.tests;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.pulp.network.udp.UdpNetwork;
import sneer.bricks.pulp.network.udp.impl.tests.UdpNetworkTest;
import sneer.bricks.pulp.network.udp.inprocess.InProcessUdpNetwork;
import sneer.foundation.brickness.testsupport.Bind;





public class InProcessUdpNetworkTest extends UdpNetworkTest {

	@Bind UdpNetwork inProcess = my(InProcessUdpNetwork.class);
	
}
