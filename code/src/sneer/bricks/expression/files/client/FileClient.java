package sneer.bricks.expression.files.client;

import java.io.File;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.identity.seals.Seal;
import basis.brickness.Brick;

@Brick
public interface FileClient {

	Download startDownload(File file, boolean isFolder, long size, long lastModified, Hash hashOfFile, Seal source);

	Download startFileDownload(File file, long size, long lastModified, Hash hashOfFile, Seal source);

	Download startFolderDownload(File folder, Hash hashOfFolder, Seal source);

	Download startFolderDeltasDownload(File folder, Hash hashOfFolder, Seal source);

}
