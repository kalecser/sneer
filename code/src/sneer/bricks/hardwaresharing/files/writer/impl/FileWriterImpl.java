package sneer.bricks.hardwaresharing.files.writer.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardwaresharing.files.map.visitors.FileMapGuide;
import sneer.bricks.hardwaresharing.files.writer.FileWriter;
import sneer.bricks.pulp.crypto.Sneer1024;


public class FileWriterImpl implements FileWriter {

	
	@Override
	public void writeAtomicallyTo(File fileOrFolder, final long lastModified, Sneer1024 hashOfContents) throws IOException {
		if (fileOrFolder.exists()) throw new IOException("File to be written already exists: " + fileOrFolder);
		
		final File dotPart = prepareDotPart(fileOrFolder);
		
		writeTo(dotPart, hashOfContents);
		
		dotPart.setLastModified(lastModified);
		rename(dotPart, fileOrFolder);
	}

	
	@Override
	public void mergeOver(File existingFolder, Sneer1024 hashOfContents) {
		check(existingFolder);
		writeTo(existingFolder, hashOfContents);
	}


	private void writeTo(File fileOrFolder, Sneer1024 hashOfContents) {
		my(FileMapGuide.class).guide(new FileWritingVisitor(fileOrFolder), hashOfContents);
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
