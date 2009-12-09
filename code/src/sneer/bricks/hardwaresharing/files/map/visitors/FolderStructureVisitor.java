package sneer.bricks.hardwaresharing.files.map.visitors;

import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;

public interface FolderStructureVisitor {


	void enterFolder();
	void leaveFolder();
	
	boolean visitFileOrFolder(String name, long lastModified, Sneer1024 hashOfContents);
	void visitFileContents(byte[] fileContents);

	
}
