package sneer.bricks.expression.files.hasher;

import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.foundation.brickness.Brick;

@Brick
public interface FolderContentsHasher {

	Sneer1024 hash(FolderContents contents);

}
