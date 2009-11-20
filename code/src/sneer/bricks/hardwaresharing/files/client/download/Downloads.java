package sneer.bricks.hardwaresharing.files.client.download;

import java.io.File;

import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.foundation.brickness.Brick;

@Brick
public interface Downloads {

	Download newFileDownload(File file, long lastModified, Sneer1024 hashOfFile);

	Download newFolderDownload(File folder, long lastModified, Sneer1024 hashOfFile);

}
