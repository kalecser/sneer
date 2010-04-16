package sneer.bricks.expression.files.map.visitors;

import sneer.bricks.hardware.cpu.crypto.Hash;

public interface FolderStructureVisitor {


	void enterFolder();
	void leaveFolder();
	
	boolean visitFileOrFolder(String name, long lastModified, Hash hashOfContents);
	void visitFileContents(byte[] fileContents);

	
}
