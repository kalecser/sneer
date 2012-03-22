package sneer.bricks.network.computers.authentication;

import java.io.IOException;

import basis.brickness.Brick;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.network.ByteArraySocket;

@Brick
public interface PublicKeyChallenges {

	boolean challenge(Seal contactsSeal, ByteArraySocket socket) throws IOException;

}
