package sneer.tests.freedom5;

import org.junit.Ignore;
import org.junit.Test;

import sneer.tests.SovereignFunctionalTestBase;

public class Freedom5TestChannels extends SovereignFunctionalTestBase {

	@Ignore
	@Test// (timeout = 13000)
	public void openControlChannel() {

		b().keepSendingMessageInControlChannel(a().ownName(), "Hello".getBytes());
		byte[] actual = a().waitForMessageInControlChannel(b().ownName());
		assertArrayEquals("Hello".getBytes(), actual);

	}

}