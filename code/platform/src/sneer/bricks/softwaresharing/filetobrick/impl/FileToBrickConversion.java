package sneer.bricks.softwaresharing.filetobrick.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.Lang.Strings;
import sneer.bricks.hardwaresharing.files.cache.visitors.FileCacheGuide;
import sneer.bricks.hardwaresharing.files.cache.visitors.FileCacheVisitor;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.softwaresharing.BrickInfo;

class FileToBrickConversion implements FileCacheVisitor {

	private final Strings _strings = my(Lang.class).strings();
	
	final Set<BrickInfo> _result = new HashSet<BrickInfo>();
	private final Deque<String> _namePath = new LinkedList<String>();
	private final Deque<Sneer1024> _hashPath = new LinkedList<Sneer1024>();
	

	FileToBrickConversion(Collection<Sneer1024> srcFolderHashes) {
		my(FileCacheGuide.class).guide(this, srcFolderHashes.iterator().next());
		System.err.println("Only doing the first above");
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
		
		_result.add(brickFound(fileName));
	}

	private BrickInfo brickFound(String fileName) {
		String packageName = _strings.join(_namePath, ".");
		String brickName = _strings.chomp(packageName + "." + fileName, ".java");
		return new BrickInfoImpl(brickName, _hashPath.peek());
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
