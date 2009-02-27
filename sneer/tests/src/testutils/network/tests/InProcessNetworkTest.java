package testutils.network.tests;

import sneer.pulp.network.Network;
import sneer.pulp.network.tests.NetworkTest;
import tests.Contribute;
import testutils.network.InProcessNetwork;

public class InProcessNetworkTest extends NetworkTest {

	// will automatically be made available in the container
	// by ContainerEnvironment
	@Contribute final Network _subject = new InProcessNetwork();

}