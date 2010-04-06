package sneer.bricks.expression.files.client.downloads;

import java.io.File;
import java.lang.ref.WeakReference;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.network.social.Contact;
import sneer.foundation.brickness.Brick;

@Brick
public interface Downloads {

	WeakReference<Download> newFileDownload(File file, long lastModified, Hash hashOfFile);
	WeakReference<Download> newFileDownload(File file, long lastModified, Hash hashOfFile, Contact source, Runnable toCallWhenFinished);

	WeakReference<Download> newFolderDownload(File folder, long lastModified, Hash hashOfFile);
	WeakReference<Download> newFolderDownload(File folder, long lastModified, Hash hashOfFile, Runnable toCallWhenFinished);

}
