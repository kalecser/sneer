package sneer.bricks.expression.files.client.downloads;

import java.io.File;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.identity.seals.Seal;
import sneer.foundation.brickness.Brick;

@Brick
public interface Downloads {

	Download newFileDownload(File file, long lastModified, Hash hashOfFile);
	Download newFileDownload(File file, long lastModified, Hash hashOfFile, Seal source, Runnable toCallWhenFinished);

	Download newFolderDownload(File folder, long lastModified, Hash hashOfFile);
	Download newFolderDownload(File folder, long lastModified, Hash hashOfFile, Runnable toCallWhenFinished);

}
