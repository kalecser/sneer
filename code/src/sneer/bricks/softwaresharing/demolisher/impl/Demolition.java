package sneer.bricks.softwaresharing.demolisher.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Deque;
import java.util.LinkedList;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.map.visitors.FileMapGuide;
import sneer.bricks.expression.files.map.visitors.FolderStructureVisitor;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.Lang.Strings;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.softwaresharing.BrickInfo;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.ProducerX;

class Demolition implements FolderStructureVisitor {

	private final Strings _strings = my(Lang.class).strings();
	
	private final CacheMap<String, BrickInfo> _bricksByName;

	private final Deque<String> _namePath = new LinkedList<String>();
	private final Deque<Sneer1024> _hashPath = new LinkedList<Sneer1024>();

	private final boolean _isCurrent;

	private IOException _firstExceptionFound;
	

	Demolition(CacheMap<String,BrickInfo> bricksByName, Sneer1024 srcFolderHash, boolean isCurrent) throws IOException {
		_bricksByName = bricksByName;
		_isCurrent = isCurrent;
		my(FileMapGuide.class).guide(this, folderContents(srcFolderHash));
		
		if (_firstExceptionFound != null) throw _firstExceptionFound;
	}


	private FolderContents folderContents(Sneer1024 srcFolderHash) {
		return my(FileMap.class).getFolderContents(srcFolderHash);
	}


	@Override
	public boolean visitFileOrFolder(String name, long lastModified, Sneer1024 hashOfContents) {
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
		final Sneer1024 packageHash = _hashPath.peekLast();

		BrickInfoImpl existingBrick = (BrickInfoImpl) _bricksByName.get(brickName, new ProducerX<BrickInfo, IOException>() { @Override public BrickInfo produce() throws IOException {
			return new BrickInfoImpl(brickName, packageHash, _isCurrent);
		}});
		
		existingBrick.addVersionIfNecessary(packageHash, _isCurrent);

		my(Logger.class).log("+++++++++++++++++++++++++++++++++++++++++++++++ Brick accumulated: " + brickName + " isCurrent: " + _isCurrent);
	}
	

	private boolean isBrickDefinition(byte[] fileContents) {
		String contents = asString(fileContents);
		return contents.contains("@Brick")
			|| contents.contains("@sneer.foundation.brickness.Brick");
	}

	
	private String asString(byte[] fileContents) {
		try {
			return new String(fileContents, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}
}
