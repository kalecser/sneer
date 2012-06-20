package sneer.tests.adapters.impl.utils.network.udp.tests;

import static basis.environments.Environments.my;
import basis.brickness.testsupport.Bind;
import sneer.bricks.network.computers.udp.UdpNetwork;
import sneer.bricks.network.computers.udp.tests.UdpNetworkTest;
import sneer.tests.adapters.impl.utils.network.udp.InProcessUdpNetwork;





public class InProcessUdpNetworkTest extends UdpNetworkTest {

	@Bind UdpNetwork inProcess = my(InProcessUdpNetwork.class);
	
}
