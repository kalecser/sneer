package sneer.bricks.snapps.web.tests;

import org.junit.Assert;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.snapps.web.impl.ContactProvider;

public class ContactProviderMock implements ContactProvider{

	private Seal seal;
	private boolean wasCalled = false;
	private String expectedNick;

	public void addContact(String nick, Seal seal) {
		this.expectedNick = nick;
		this.seal = seal;
	}

	public boolean wasCalled() {
		return wasCalled;
	}

	@Override
	public Seal getSealForNickOrNull(String nickName) {
		wasCalled  = true;
		Assert.assertEquals(expectedNick, nickName);
		return seal;
	}
	
	

}
