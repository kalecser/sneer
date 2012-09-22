package sneer.bricks.snapps.web.tests;

import org.junit.Test;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.snapps.web.impl.SealForUrl;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class SealForUrlTest extends BrickTestBase{

	@Test
	public void requestUrlForJhon(){
		assertUrlMatchesNick("/john/bla", "john");
	}

	@Test
	public void requestUrlForMary(){
		assertUrlMatchesNick("/mary", "mary");
	}

	private void assertUrlMatchesNick(String url, String nick) {
		ContactProviderMock contactProviderMock = new ContactProviderMock();
		Seal expected = new Seal(new byte[]{1,2,3,4});
		contactProviderMock.addContact(nick,expected);
		SealForUrl subject = new SealForUrl(contactProviderMock);
		Seal actualSeal = subject.getSealForUrlOrNull(url);
		assertEquals(expected, actualSeal);
		assertTrue(contactProviderMock.wasCalled());
	}
	
}
