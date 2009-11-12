package sneer.bricks.hardwaresharing.files.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.bricks.hardwaresharing.files.writer.AtomicFileWriter;
import sneer.bricks.pulp.crypto.Sneer1024;

public class LocalCopyTest extends FileCopyTestBase {

	@Override
	protected void copyFileFromFileMap(Sneer1024 hashOfContents, File destination) throws IOException {
		copyFromFileMap(hashOfContents, destination);
	}


	@Override
	protected void copyFolderFromFileMap(Sneer1024 hashOfContents, File destination) throws IOException {
		copyFromFileMap(hashOfContents, destination);
	}


	private void copyFromFileMap(Sneer1024 hashOfContents, File destination) throws IOException {
		File file = my(FileMap.class).getFile(hashOfContents);
		if (file != null) {
			copyFile(destination, file);
			return;
		}
		
		copyFolder(hashOfContents, destination);
	}


	private void copyFolder(Sneer1024 hashOfContents, File destination) throws IOException {
		FolderContents folder = my(FileMap.class).getFolder(hashOfContents);
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
