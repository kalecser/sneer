package sneer.bricks.network.computers.udp.inprocess.tests;

import static basis.environments.Environments.my;
import basis.brickness.testsupport.Bind;
import sneer.bricks.network.computers.udp.UdpNetwork;
import sneer.bricks.network.computers.udp.inprocess.InProcessUdpNetwork;
import sneer.bricks.network.computers.udp.tests.UdpNetworkTest;





public class InProcessUdpNetworkTest extends UdpNetworkTest {

	@Bind UdpNetwork inProcess = my(InProcessUdpNetwork.class);
	
}
