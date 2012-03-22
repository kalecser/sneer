package sneer.bricks.softwaresharing.demolisher.impl;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import basis.lang.Functor;

import sneer.bricks.expression.files.hasher.FolderContentsHasher;
import sneer.bricks.expression.files.map.visitors.FileMapGuide;
import sneer.bricks.expression.files.map.visitors.FolderStructureVisitor;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.softwaresharing.BrickVersion;
import sneer.bricks.softwaresharing.FileVersion;

class BrickVersionImpl implements BrickVersion {

	private final FolderContents _contents;
	private final List<FileVersion> _files;
	
	private final Register<Status> _status;
	private boolean _stagedForExecution;
	private final Functor<String, byte[]> _currentContentsFinder;
	private final ListRegister<Contact> _users;
	
	BrickVersionImpl(Hash hashOfPackage, boolean isCurrent, Functor<String, byte[]> currentContentsFinder, BrickVersion current) throws IOException {
		_currentContentsFinder = currentContentsFinder;
		_status = my(Signals.class).newRegister(isCurrent ? Status.CURRENT : Status.DIFFERENT);
		_users = my(CollectionSignals.class).newListRegister();
		_contents = BrickFilter.retrieveOnlyFilesFromThisBrick(hashOfPackage);
		_files = findFiles();
		addMissingFiles(current);
	}


	private void addMissingFiles(BrickVersion current) {
		if (current == null)
			return;
		
		for (FileVersion currentFile : current.files())
			if (file(currentFile.relativePath()) == null)
				addMissingFile(currentFile);
	}


	private void addMissingFile(FileVersion currentFile) {
		_files.add(new FileVersionImpl(currentFile.relativePath(), null, _currentContentsFinder, currentFile.lastModified(), false));
	}


	@Override public List<FileVersion> files() { return _files; }
	@Override public Hash hash() { return my(FolderContentsHasher.class).hash(_contents); }
	@Override public boolean isChosenForExecution() { return _stagedForExecution; }
	@Override public Signal<Status> status() { return _status.output(); }

	
	@Override
	public ListSignal<Contact> users() {
		return _users.output();
	}

	@Override
	public long publicationDate() {
		return 0;
	}

	@Override
	public void setRejected(boolean rejected) {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
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
		_status.setter().consume(Status.CURRENT);
	}
	
	
	private boolean isCurrent() {
		return _status.output().currentValue() == Status.CURRENT;
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
			_visitedFiles.add(new FileVersionImpl(path, fileContents, _currentContentsFinder, _lastModified, isCurrent()));
			_path.removeLast();
		}

		private String pathToString() {
			return my(Lang.class).strings().join(_path, "/");
		}

	}

	@Override
	public FileVersion file(String relativePath) {
		for (FileVersion version : files())
			if (version.relativePath().equals(relativePath))
				return version;
		return null;
	}

	public void addUser(Contact user) {
		if (_users.output().currentIndexOf(user) != -1)
			throw new IllegalArgumentException(user.nickname().currentValue());
		_users.add(user);
	}
}
