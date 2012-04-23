package sneer.tests.adapters.impl.utils.network.tests;

import basis.brickness.testsupport.Bind;
import sneer.bricks.network.computers.tcp.TcpNetwork;
import sneer.bricks.network.computers.tcp.tests.Network2010Test;
import sneer.tests.adapters.impl.utils.network.InProcessNetwork;

public class InProcessNetworkTest extends Network2010Test {

	@Bind final TcpNetwork _subject = new InProcessNetwork();

}
