package sneer.tests.adapters.impl.utils.network.tests;

import sneer.bricks.pulp.network.Network;
import sneer.bricks.pulp.network.tests.NetworkTest;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.tests.adapters.impl.utils.network.InProcessNetwork;

public class InProcessNetworkTest extends NetworkTest {

	// will automatically be made available in the container
	// by ContainerEnvironment
	@Bind final Network _subject = new InProcessNetwork();

}
