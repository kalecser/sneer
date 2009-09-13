package sneer.bricks.hardwaresharing.files.cache.visitors;

import sneer.bricks.pulp.crypto.Sneer1024;

public interface FileCacheVisitor {


	void enterFolder();
	void leaveFolder();
	
	boolean visitFileOrFolder(String name, long lastModified, Sneer1024 hashOfContents);
	void visitFileContents(byte[] fileContents);

	
}
