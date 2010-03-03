package sneer.bricks.expression.files.client.downloads;

import java.io.File;

import sneer.bricks.hardware.cpu.crypto.Sneer1024;
import sneer.foundation.brickness.Brick;

@Brick
public interface Downloads {

	Download newFileDownload(File file, long lastModified, Sneer1024 hashOfFile);
	Download newFileDownload(File file, long lastModified, Sneer1024 hashOfFile, Runnable toCallWhenFinished);

	Download newFolderDownload(File folder, long lastModified, Sneer1024 hashOfFile);
	Download newFolderDownload(File folder, long lastModified, Sneer1024 hashOfFile, Runnable toCallWhenFinished);

}
