package sneer.tests.adapters.impl.utils.network.tests;

import basis.brickness.testsupport.Bind;
import sneer.bricks.pulp.network.Network2010;
import sneer.bricks.pulp.network.tests.Network2010Test;
import sneer.tests.adapters.impl.utils.network.InProcessNetwork;

public class InProcessNetworkTest extends Network2010Test {

	@Bind final Network2010 _subject = new InProcessNetwork();

}
