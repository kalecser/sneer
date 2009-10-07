package sneer.bricks.hardwaresharing.files.hasher;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.foundation.brickness.Brick;

@Brick
public interface Hasher {

	Sneer1024 hash(byte[] contents);

	Sneer1024 hash(FolderContents contents);

	Sneer1024 hash(File file) throws IOException;

}
