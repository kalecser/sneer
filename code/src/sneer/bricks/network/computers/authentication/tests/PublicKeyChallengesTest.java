package sneer.bricks.network.computers.authentication.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;

import org.jmock.Expectations;
import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.computers.authentication.PublicKeyChallenges;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.foundation.lang.arrays.ImmutableByteArray;

public class PublicKeyChallengesTest extends BrickTestBase {

	private final PublicKeyChallenges _subject = my(PublicKeyChallenges.class);
	
	@Ignore
	@Test
	public void challenge() throws IOException {
		final ByteArraySocket socket = mock(ByteArraySocket.class);
		
		checking(new Expectations() {{
//			oneOf(socket).read(); will(returnValue(encodedPublicKey()));
		}});
		
		assertTrue(_subject.challenge(seal(), socket));
	}

	
	private Seal seal() {
		return new Seal(new ImmutableByteArray(new byte[] {42}));
	}
	
}
