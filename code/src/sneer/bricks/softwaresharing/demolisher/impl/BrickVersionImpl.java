package sneer.bricks.softwaresharing.demolisher.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import sneer.bricks.expression.files.hasher.FolderContentsHasher;
import sneer.bricks.expression.files.map.visitors.FileMapGuide;
import sneer.bricks.expression.files.map.visitors.FolderStructureVisitor;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.softwaresharing.BrickVersion;
import sneer.bricks.softwaresharing.FileVersion;

class BrickVersionImpl implements BrickVersion {

	private final FolderContents _contents;
	private final List<FileVersion> _files;
	
	private Status _status;
	private boolean _stagedForExecution;
	
	BrickVersionImpl(Hash hashOfPackage, boolean isCurrent) throws IOException {
		_status = isCurrent ? Status.CURRENT : Status.DIFFERENT;
		_contents = BrickFilter.retrieveOnlyFilesFromThisBrick(hashOfPackage);
		_files = findFiles();
	}


	@Override public List<FileVersion> files() { return _files; }
	@Override public Hash hash() { return my(FolderContentsHasher.class).hash(_contents); }
	@Override public boolean isChosenForExecution() { return _stagedForExecution; }
	@Override public Status status() { return _status; }

	
	@Override
	public List<String> users() {
		return new ArrayList<String>();
	}

	@Override
	public long publicationDate() {
		return 0;
	}

	@Override
	public void setRejected(boolean rejected) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}


	void setChosenForExecution(boolean value) {
		_stagedForExecution = value;
	}

	private List<FileVersion> findFiles() throws IOException {
		Visitor visitor = new Visitor();
		my(FileMapGuide.class).guide(visitor, folderContents());
		return visitor._visitedFiles;
	}


	private FolderContents folderContents() {
		return _contents;
	}

	
	void setCurrent() {
		_status = Status.CURRENT;
	}
	
	
	private boolean isCurrent() {
		return _status == Status.CURRENT;
	}


	private class Visitor implements FolderStructureVisitor {

		List<FileVersion> _visitedFiles = new ArrayList<FileVersion>();
		
		private Deque<String> _path = new LinkedList<String>();
		private long _lastModified;

		
		@Override
		public boolean visitFileOrFolder(String name, long lastModified, Hash hashOfContents) {
			_path.addLast(name);
			_lastModified = lastModified;
			return true;
		}
		
		@Override
		public void enterFolder() {}

		@Override
		public void leaveFolder() {
			if (_path.isEmpty()) return;
			_path.removeLast();
		}

		@Override
		public void visitFileContents(byte[] fileContents) {
			String path = pathToString();
			byte[] fileContentsInCurrentVersion = isCurrent() ? fileContents : fileContentsInCurrentVersion(path);
			_visitedFiles.add(new FileVersionImpl(path, fileContents, fileContentsInCurrentVersion, _lastModified, isCurrent()));
			_path.removeLast();
		}

		private String pathToString() {
			return my(Lang.class).strings().join(_path, "/");
		}

	}


	private byte[] fileContentsInCurrentVersion(@SuppressWarnings("unused") String path) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}


}
