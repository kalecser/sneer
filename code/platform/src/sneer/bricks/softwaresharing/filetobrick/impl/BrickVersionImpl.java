package sneer.bricks.softwaresharing.filetobrick.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import sneer.bricks.hardwaresharing.files.cache.visitors.FileCacheGuide;
import sneer.bricks.hardwaresharing.files.cache.visitors.FileCacheVisitor;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.softwaresharing.BrickVersion;
import sneer.bricks.softwaresharing.FileVersion;

class BrickVersionImpl implements BrickVersion {


	private final Sneer1024 _hash;
	private boolean _stagedForExecution;
	private List<FileVersion> _files;
	private Status _status;

	
	BrickVersionImpl(Sneer1024 hash, boolean isCurrent) {
		_hash = hash;
		_status = isCurrent ? Status.CURRENT : Status.DIFFERENT;
	}

	@Override
	public List<FileVersion> files() {
		if (_files == null) _files = findFiles();
		return _files;
	}

	@Override
	public Sneer1024 hash() {
		return _hash;
	}

	@Override
	public boolean isStagedForExecution() {
		return _stagedForExecution;
	}

	@Override
	public int unknownUsers() {
		return 0;
	}
	
	@Override
	public List<String> knownUsers() {
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

	@Override
	public Status status() {
		return _status;
	}
	

	void setStagedForExecution(boolean value) {
		_stagedForExecution = value;
	}

	private List<FileVersion> findFiles() {
		Visitor visitor = new Visitor();
		my(FileCacheGuide.class).guide(visitor, _hash);
		return visitor._visitedFiles;
	}

	
	void setCurrent() {
		_status = Status.CURRENT;
	}
	
	
	private boolean isCurrent() {
		return _status == Status.CURRENT;
	}


	private class Visitor implements FileCacheVisitor {

		List<FileVersion> _visitedFiles = new ArrayList<FileVersion>();
		
		private Deque<String> _path = new LinkedList<String>();
		private long _lastModified;

		
		@Override
		public void visitFileOrFolder(String name, long lastModified, Sneer1024 hashOfContents) {
			_path.add(name);
			_lastModified = lastModified;
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
