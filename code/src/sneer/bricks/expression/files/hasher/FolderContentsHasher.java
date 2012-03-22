package sneer.bricks.expression.files.hasher;

import basis.brickness.Brick;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.crypto.Hash;

@Brick
public interface FolderContentsHasher {

	Hash hash(FolderContents contents);

}
