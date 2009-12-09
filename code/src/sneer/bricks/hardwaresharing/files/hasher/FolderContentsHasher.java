package sneer.bricks.hardwaresharing.files.hasher;

import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.foundation.brickness.Brick;

@Brick
public interface FolderContentsHasher {

	Sneer1024 hash(FolderContents contents);

}
