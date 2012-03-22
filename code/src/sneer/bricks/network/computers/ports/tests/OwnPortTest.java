package sneer.bricks.network.computers.ports.tests;

import static basis.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.network.computers.ports.OwnPort;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class OwnPortTest extends BrickTestBase {

	@Test (timeout = 2000)
	public void setOwnPort() throws Exception {
		setOwnPort(42);
		assertEquals(42, ownPort());
	}

	private void setOwnPort(int port) {
		my(Attributes.class).myAttributeSetter(OwnPort.class).consume(port);
	}

	private int ownPort() {
		return my(Attributes.class).myAttributeValue(OwnPort.class).currentValue().intValue();
	}

}
