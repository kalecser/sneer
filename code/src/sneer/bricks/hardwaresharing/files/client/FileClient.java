package sneer.bricks.hardwaresharing.files.client;

import java.io.File;

import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.foundation.brickness.Brick;

@Brick
public interface FileClient {

	Download startFileDownload(File file, long lastModified, Sneer1024 hashOfFile);
	Download startFileDownload(File file, Sneer1024 hashOfFile);

	Download startFolderDownload(File folder, long lastModified, Sneer1024 hashOfFolder);
	Download startFolderDownload(File folder, Sneer1024 hashOfFolder);

}
