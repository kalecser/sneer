package sneer.bricks.network.computers.authentication;

import java.io.IOException;

import basis.brickness.Brick;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.computers.tcp.ByteArraySocket;

@Brick
public interface PublicKeyChallenges {

	boolean challenge(Seal contactsSeal, ByteArraySocket socket) throws IOException;

}
