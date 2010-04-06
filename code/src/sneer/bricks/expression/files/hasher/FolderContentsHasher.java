package sneer.bricks.expression.files.hasher;

import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.foundation.brickness.Brick;

@Brick
public interface FolderContentsHasher {

	Hash hash(FolderContents contents);

}
