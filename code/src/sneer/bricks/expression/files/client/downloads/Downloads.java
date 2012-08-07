package sneer.bricks.expression.files.client.downloads;

import java.io.File;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.identity.seals.Seal;
import basis.brickness.Brick;

@Brick
public interface Downloads {

	Download newFileDownload(File file, long size, long lastModified, Hash hashOfFile, Seal source);

	Download newFolderDownload(File folder, Hash hashOfFolder, Seal source, boolean copyLocalFiles);

}
