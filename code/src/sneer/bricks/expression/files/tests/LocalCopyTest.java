package sneer.bricks.expression.files.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.expression.files.writer.atomic.AtomicFileWriter;
import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.bricks.hardware.io.IO;

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
		FolderContents folder = my(FileMap.class).getFolderContents(hashOfContents);
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
