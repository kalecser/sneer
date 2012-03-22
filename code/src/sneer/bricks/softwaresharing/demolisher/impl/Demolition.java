package sneer.bricks.softwaresharing.demolisher.impl;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Deque;
import java.util.LinkedList;

import basis.lang.CacheMap;
import basis.lang.Producer;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.map.visitors.FileMapGuide;
import sneer.bricks.expression.files.map.visitors.FolderStructureVisitor;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.Lang.Strings;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.network.social.Contact;
import sneer.bricks.softwaresharing.BrickHistory;

class Demolition implements FolderStructureVisitor {

	private final Strings _strings = my(Lang.class).strings();
	
	private final CacheMap<String, BrickHistory> _bricksByName;

	private final Deque<String> _namePath = new LinkedList<String>();
	private final Deque<Hash> _hashPath = new LinkedList<Hash>();

	private final Contact _owner;

	private IOException _firstExceptionFound;
	
	Demolition(CacheMap<String,BrickHistory> bricksByName, Hash srcFolderHash, Contact owner) throws IOException {
		_bricksByName = bricksByName;
		_owner = owner;
		my(FileMapGuide.class).guide(this, folderContents(srcFolderHash));
		
		if (_firstExceptionFound != null) throw _firstExceptionFound;
	}


	private FolderContents folderContents(Hash srcFolderHash) {
		return my(FileMap.class).getFolderContents(srcFolderHash);
	}


	@Override
	public boolean visitFileOrFolder(String name, long lastModified, Hash hashOfContents) {
		if (name.equals("impl")) return false;
		if (name.equals("tests")) return false;
		
		_namePath.add(name);
		_hashPath.add(hashOfContents);
		return true;
	}

	
	@Override
	public void visitFileContents(byte[] fileContents) {
		String fileName = _namePath.peekLast();
		_namePath.removeLast();
		_hashPath.removeLast();

		if (!fileName.endsWith(".java")) return;
		if (!isBrickDefinition(fileContents)) return;
		
		try {
			accumulateBrick(fileName);
		} catch (IOException e) {
			if (_firstExceptionFound == null)
				_firstExceptionFound = e;
		}
	}


	@Override public void enterFolder() {}
	
	
	@Override public void leaveFolder() {
		if (_namePath.isEmpty()) return;
		
		_namePath.removeLast();
		_hashPath.removeLast();
	}

	
	private void accumulateBrick(String fileName) throws IOException {
		String packageName = _strings.join(_namePath, ".");
		final String brickName = _strings.chomp(packageName + "." + fileName, ".java");
		final Hash packageHash = _hashPath.peekLast();

		BrickHistoryImpl existingBrick = (BrickHistoryImpl) _bricksByName.get(brickName, new Producer<BrickHistory>() { @Override public BrickHistory produce() {
			return new BrickHistoryImpl(brickName);
		}});
		
		existingBrick.addVersionIfNecessary(packageHash, _owner);

		my(Logger.class).log("++++++++++++++++++++ Brick accumulated: " + brickName + " owner: " + _owner);
	}
	

	private boolean isBrickDefinition(byte[] fileContents) {
		String contents = asString(fileContents);
		return contents.contains("@Brick")
			|| contents.contains("@basis.brickness.Brick");
	}

	
	private String asString(byte[] fileContents) {
		try {
			return new String(fileContents, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}
}
