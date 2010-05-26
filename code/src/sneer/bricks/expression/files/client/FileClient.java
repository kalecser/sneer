package sneer.bricks.expression.files.client;

import java.io.File;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.identity.seals.Seal;
import sneer.foundation.brickness.Brick;

@Brick
public interface FileClient {

	Download startFileDownload(File file, long lastModified, Hash hashOfFile, Seal source);

	Download startFolderDownload(File folder, long lastModified, Hash hashOfFolder);

}
