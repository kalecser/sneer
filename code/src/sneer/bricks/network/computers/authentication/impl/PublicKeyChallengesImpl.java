package sneer.bricks.network.computers.authentication.impl;

import java.io.IOException;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.computers.authentication.PublicKeyChallenges;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.foundation.lang.exceptions.NotImplementedYet;

class PublicKeyChallengesImpl implements PublicKeyChallenges {

	@Override
	public boolean challenge(Seal contactsSeal, ByteArraySocket socket) throws IOException {
		//socket.write(array);
		@SuppressWarnings("unused")
		byte[] publicKeyBytes = socket.read();
		throw new NotImplementedYet();
	}

}
