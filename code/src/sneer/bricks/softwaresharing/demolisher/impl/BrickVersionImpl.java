package sneer.bricks.softwaresharing.demolisher.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.map.visitors.FileMapGuide;
import sneer.bricks.expression.files.map.visitors.FolderStructureVisitor;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.bricks.softwaresharing.BrickVersion;
import sneer.bricks.softwaresharing.FileVersion;

class BrickVersionImpl implements BrickVersion {

	private final Sneer1024 _hash;
	private final List<FileVersion> _files;
	
	private Status _status;
	private boolean _stagedForExecution;
	
	BrickVersionImpl(Sneer1024 hashOfPackage, boolean isCurrent) throws IOException {
		_hash = BrickFilter.cacheOnlyFilesFromThisBrick(hashOfPackage);
		_files = findFiles();
		_status = isCurrent ? Status.CURRENT : Status.DIFFERENT;
	}


	@Override public List<FileVersion> files() { return _files; }
	@Override public Sneer1024 hash() { return _hash; }
	@Override public boolean isStagedForExecution() { return _stagedForExecution; }
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


	void setStagedForExecution(boolean value) {
		_stagedForExecution = value;
	}

	private List<FileVersion> findFiles() throws IOException {
		Visitor visitor = new Visitor();
		my(FileMapGuide.class).guide(visitor, folderContents());
		return visitor._visitedFiles;
	}


	private FolderContents folderContents() {
		return my(FileMap.class).getFolder(_hash);
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
		public boolean visitFileOrFolder(String name, long lastModified, Sneer1024 hashOfContents) {
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
			_visitedFiles.add(new FileVersionImpl((List<String>)_path, fileContents, _lastModified, isCurrent()));
			_path.removeLast();
		}

	}


}
