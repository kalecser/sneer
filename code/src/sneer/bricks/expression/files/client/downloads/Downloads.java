package sneer.bricks.expression.files.client.downloads;

import java.io.File;
import java.lang.ref.WeakReference;

import sneer.bricks.hardware.cpu.crypto.Sneer1024;
import sneer.foundation.brickness.Brick;

@Brick
public interface Downloads {

	WeakReference<Download> newFileDownload(File file, long lastModified, Sneer1024 hashOfFile);
	WeakReference<Download> newFileDownload(File file, long lastModified, Sneer1024 hashOfFile, Runnable toCallWhenFinished);

	WeakReference<Download> newFolderDownload(File folder, long lastModified, Sneer1024 hashOfFile);
	WeakReference<Download> newFolderDownload(File folder, long lastModified, Sneer1024 hashOfFile, Runnable toCallWhenFinished);

}
