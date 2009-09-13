package sneer.bricks.hardwaresharing.files.hasher;

import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.foundation.brickness.Brick;

@Brick
public interface Hasher {

	Sneer1024 hash(byte[] contents);

	Sneer1024 hash(FolderContents contents);


}
