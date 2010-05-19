package sneer.bricks.identity.name.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.identity.name.OwnName;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.software.folderconfig.testsupport.BrickTestWithFiles;

public class OwnNameTest extends BrickTestWithFiles {

	@Test
	public void test() throws Exception {
		setOwnName("Neide");
		assertEquals("Neide", ownName());

		setOwnName("Mr. Mister");
		assertEquals("Mr. Mister", ownName());
	}

	private void setOwnName(String name) {
		my(Attributes.class).myAttributeSetter(OwnName.class).consume(name);
	}

	private String ownName() {
		return my(Attributes.class).myAttributeValue(OwnName.class).currentValue();
	}

}
