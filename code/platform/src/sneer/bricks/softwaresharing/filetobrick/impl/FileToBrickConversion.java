package sneer.bricks.softwaresharing.filetobrick.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.UnsupportedEncodingException;
import java.util.Deque;
import java.util.LinkedList;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.Lang.Strings;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardwaresharing.files.cache.visitors.FileCacheGuide;
import sneer.bricks.hardwaresharing.files.cache.visitors.FileCacheVisitor;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.softwaresharing.BrickInfo;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;

class FileToBrickConversion implements FileCacheVisitor {

	private final Strings _strings = my(Lang.class).strings();
	
	private final CacheMap<String, BrickInfo> _bricksByName;

	private final Deque<String> _namePath = new LinkedList<String>();
	private final Deque<Sneer1024> _hashPath = new LinkedList<Sneer1024>();

	private final boolean _isCurrent;
	

	FileToBrickConversion(CacheMap<String,BrickInfo> bricksByName, Sneer1024 srcFolderHash, boolean isCurrent) {
		_bricksByName = bricksByName;
		_isCurrent = isCurrent;
		my(FileCacheGuide.class).guide(this, srcFolderHash);
	}


	@Override
	public void visitFileOrFolder(String name, long lastModified, Sneer1024 hashOfContents) {
		_namePath.add(name);
		_hashPath.add(hashOfContents);
	}

	
	@Override
	public void visitFileContents(byte[] fileContents) {
		String fileName = _namePath.peekLast();

		_namePath.removeLast();
		_hashPath.removeLast();

		if (!fileName.endsWith(".java")) return;

		if (!isBrickDefinition(fileContents)) return;
		
		accumulateBrick(fileName);
	}


	private void accumulateBrick(String fileName) {
		String packageName = _strings.join(_namePath, ".");
		final String brickName = _strings.chomp(packageName + "." + fileName, ".java");

		BrickInfoImpl brick = (BrickInfoImpl) _bricksByName.get(brickName, new Producer<BrickInfo>() { @Override public BrickInfo produce() {
			return new BrickInfoImpl(brickName);
		}});
		
		Sneer1024 versionHash = _hashPath.peekLast();
		brick.addVersion(versionHash, _isCurrent);

		my(Logger.class).log("+++++++++++++++++++++++++++++++++++++++++++++++ Brick accumulated: " + brickName + " isCurrent: " + _isCurrent + " version: " + versionHash);
	}
	

	private boolean isBrickDefinition(byte[] fileContents) {
		String contents = asString(fileContents);
		return contents.contains("@Brick")
			|| contents.contains("@sneer.foundation.brickness.Brick");
	}


	@Override public void enterFolder() {}
	
	
	@Override public void leaveFolder() {
		if (_namePath.isEmpty()) return;
		
		_namePath.removeLast();
		_hashPath.removeLast();
	}
	
	private String asString(byte[] fileContents) {
		try {
			return new String(fileContents, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}
}
