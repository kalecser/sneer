package sneer.bricks.pulp.network.udp.inprocess.tests;

import static basis.environments.Environments.my;
import basis.brickness.testsupport.Bind;
import sneer.bricks.pulp.network.udp.UdpNetwork;
import sneer.bricks.pulp.network.udp.impl.tests.UdpNetworkTest;
import sneer.bricks.pulp.network.udp.inprocess.InProcessUdpNetwork;





public class InProcessUdpNetworkTest extends UdpNetworkTest {

	@Bind UdpNetwork inProcess = my(InProcessUdpNetwork.class);
	
}
