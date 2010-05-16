package sneer.bricks.expression.files.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.expression.files.writer.atomic.AtomicFileWriter;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.io.IO;

public class LocalCopyTest extends FileCopyTestBase {

	@Override
	protected void copyFileFromFileMap(Hash hashOfContents, File destination) throws IOException {
		copyFromFileMap(hashOfContents, destination);
	}


	@Override
	protected void copyFolderFromFileMap(Hash hashOfContents, File destination) throws IOException {
		copyFromFileMap(hashOfContents, destination);
	}


	private void copyFromFileMap(Hash hashOfContents, File destination) throws IOException {
		FolderContents folder = my(FileMap.class).getFolderContents(hashOfContents);
		if (folder == null)
			copyFile(destination, new File(my(FileMap.class).getFile(hashOfContents)));
		else
			copyFolder(folder, destination);
	}


	private void copyFolder(FolderContents folder, File destination) throws IOException {
		my(AtomicFileWriter.class).writeAtomicallyTo(destination, -1, folder);
	}


	private void copyFile(File destination, File file) throws IOException {
		my(AtomicFileWriter.class).writeAtomicallyTo(destination, anyReasonableDate(), bytes(file));
	}


	private byte[] bytes(File file) throws IOException {
		return my(IO.class).files().readBytes(file);
	}


	private long anyReasonableDate() {
		return System.currentTimeMillis();
	}


}
