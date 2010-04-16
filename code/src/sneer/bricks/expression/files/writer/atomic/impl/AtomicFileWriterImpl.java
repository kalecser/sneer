package sneer.bricks.expression.files.writer.atomic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.expression.files.writer.atomic.AtomicFileWriter;
import sneer.bricks.expression.files.writer.folder.FolderContentsWriter;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.files.atomic.dotpart.DotParts;


class AtomicFileWriterImpl implements AtomicFileWriter {


	@Override
	public void writeAtomicallyTo(File file, long lastModified, byte[] contents) throws IOException {
		doWriteAtomicallyTo(file, lastModified, contents);
	}


	@Override
	public void writeAtomicallyTo(File folder, long lastModified, FolderContents contents) throws IOException {
		doWriteAtomicallyTo(folder, lastModified, contents);
	}


	private void doWriteAtomicallyTo(File fileOrFolder, final long lastModified, Object contents) throws IOException {
		if (fileOrFolder.exists())
			throw new IOException("File or folder to be written already exists: " + fileOrFolder);

		final File dotPart = my(DotParts.class).openDotPartFor(fileOrFolder);
		writeTo(dotPart, contents);
		my(DotParts.class).closeDotPart(dotPart, lastModified);
	}


	private void writeTo(File fileOrFolder, Object contents) throws IOException {
		if (contents instanceof FolderContents) {
			my(FolderContentsWriter.class).writeToFolder(fileOrFolder, (FolderContents)contents);
			return;
		}

		writeToFile(fileOrFolder, (byte[])contents);
	}
	
	
	private void writeToFile(File file, byte[] contents) throws IOException {
		my(IO.class).files().writeByteArrayToFile(file, contents);
	}


}
