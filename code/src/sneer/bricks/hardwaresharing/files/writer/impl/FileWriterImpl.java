package sneer.bricks.hardwaresharing.files.writer.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardwaresharing.files.map.visitors.FileMapGuide;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.bricks.hardwaresharing.files.writer.FileWriter;


public class FileWriterImpl implements FileWriter {

	
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
		
		final File dotPart = prepareDotPart(fileOrFolder);
		
		writeTo(dotPart, contents);
		
		if (lastModified != -1)	dotPart.setLastModified(lastModified);
		rename(dotPart, fileOrFolder);
	}

	
	@Override
	public void mergeOver(File existingFolder, FolderContents contents) throws IOException {
		check(existingFolder);
		writeToFolder(existingFolder, contents);
	}


	private void writeTo(File fileOrFolder, Object contents) throws IOException {
		if (contents instanceof FolderContents) {
			writeToFolder(fileOrFolder, (FolderContents)contents);
			return;
		}

		if (contents instanceof byte[]) {
			writeToFile(fileOrFolder, (byte[])contents);
			return;
		}
		
		throw new IllegalStateException();
	}
	
	
	private void writeToFile(File file, byte[] contents) throws IOException {
		my(IO.class).files().writeByteArrayToFile(file, contents);
	}


	private void writeToFolder(File folder, FolderContents contents) throws IOException {
		my(FileMapGuide.class).guide(new FileWritingVisitor(folder), contents);
	}


	private void check(final File existingFolder) {
		if (!existingFolder.isDirectory()) throw new IllegalArgumentException("existingFolder must be a folder: " + existingFolder);
		if (!existingFolder.exists()) throw new IllegalArgumentException("Folder does not exist: " + existingFolder);
	}

	
	private File prepareDotPart(File fileOrFolder) throws IOException {
		File result = new File(fileOrFolder.getParent(), fileOrFolder.getName() + ".part");
		my(IO.class).files().forceDelete(result);
		return result;
	}


	private void rename(File file, File newName) throws IOException {
		if (!file.renameTo(newName)) throw new IOException("Unable to rename .part file/folder to actual file/folder: " + file);
	}

}
