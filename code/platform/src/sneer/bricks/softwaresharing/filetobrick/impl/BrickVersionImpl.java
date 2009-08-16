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

	
	BrickVersionImpl(Sneer1024 hash) {
		_hash = hash;
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
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}
	
	@Override
	public List<String> knownUsers() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public long publicationDate() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public void setRejected(boolean rejected) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public Status status() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	void setStagedForExecution(boolean value) {
		_stagedForExecution = value;
	}

	private List<FileVersion> findFiles() {
		Visitor visitor = new Visitor();
		my(FileCacheGuide.class).guide(visitor, _hash);
		return visitor._files;
	}

	
	private static class Visitor implements FileCacheVisitor {

		List<FileVersion> _files = new ArrayList<FileVersion>();
		
		private Deque<String> _path = new LinkedList<String>();

		
		@Override
		public void visitFileOrFolder(String name, long lastModified, Sneer1024 hashOfContents) {
			_path.add(name);
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
			_files.add(new FileVersionImpl((List<String>)_path, fileContents));
			_path.removeLast();
		}

	}


}
