package sneer.bricks.expression.files.map.visitors.impl;

import static basis.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.map.visitors.FolderStructureVisitor;
import sneer.bricks.expression.files.protocol.FileOrFolder;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.io.IO;

class GuidedTour {
	
	private final FolderStructureVisitor _visitor;

	GuidedTour(FolderStructureVisitor visitor, FolderContents contents) throws IOException {
		_visitor = visitor;
		showFolder(contents);
	}


	private void showContents(Hash hashOfContents) throws IOException {
		String file = getExistingFile(hashOfContents);
		if (file != null) {
			showFile(new File(file));
			return;
		}

		FolderContents folder = my(FileMap.class).getFolderContents(hashOfContents);
		if (folder != null) {
			showFolder(folder);
			return;
		}
		
		throw new IllegalStateException("Contents not found in " + FileMap.class.getSimpleName() + " for hash: " + hashOfContents);
	}
	
	private String getExistingFile(Hash hashOfContents) {
		for (String file : my(FileMap.class).getFiles(hashOfContents))
			if (new File(file).exists())
				return file;
		return null;
	}

	private void showFile(File file) throws IOException {
		showFile(my(IO.class).files().readBytes(file));
	}


	private void showFile(byte[] contents) {
		_visitor.visitFileContents(contents);
	}
	

	private void showFolder(FolderContents folderContents) throws IOException {
		_visitor.enterFolder();
			
		for (FileOrFolder fileOrFolder : folderContents.contents)
			showFileOrFolder(fileOrFolder);

		_visitor.leaveFolder();
	}


	private void showFileOrFolder(FileOrFolder fileOrFolder) throws IOException {
		if (shouldVisit(fileOrFolder))
			showContents(fileOrFolder.hashOfContents);
	}


	private boolean shouldVisit(FileOrFolder fileOrFolder) {
		return _visitor.visitFileOrFolder(
			fileOrFolder.name,
			fileOrFolder.lastModified,
			fileOrFolder.hashOfContents);
	}

}
