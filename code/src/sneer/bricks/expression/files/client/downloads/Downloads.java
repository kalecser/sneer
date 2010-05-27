package sneer.bricks.expression.files.client.downloads;

import java.io.File;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.identity.seals.Seal;
import sneer.foundation.brickness.Brick;

@Brick
public interface Downloads {

	Download newFileDownload(File file, long lastModified, Hash hashOfFile, Seal source, Runnable toCallWhenFinished);

	Download newFolderDownload(File folder, Hash hashOfFile, Runnable toCallWhenFinished);

}
