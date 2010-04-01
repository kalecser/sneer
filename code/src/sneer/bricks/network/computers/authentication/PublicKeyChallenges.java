package sneer.bricks.network.computers.authentication;

import java.io.IOException;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.foundation.brickness.Brick;

@Brick
public interface PublicKeyChallenges {

	boolean challenge(Seal contactsSeal, ByteArraySocket socket) throws IOException;

}
