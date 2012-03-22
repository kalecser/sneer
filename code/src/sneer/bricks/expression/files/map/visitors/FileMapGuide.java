package sneer.bricks.expression.files.map.visitors;

import java.io.IOException;

import basis.brickness.Brick;

import sneer.bricks.expression.files.protocol.FolderContents;

@Brick
public interface FileMapGuide {

	void guide(FolderStructureVisitor visitor, FolderContents contents) throws IOException;
	
}

