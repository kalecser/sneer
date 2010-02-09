package sneer.bricks.expression.files.map.visitors;

import java.io.IOException;

import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.foundation.brickness.Brick;

@Brick
public interface FileMapGuide {

	void guide(FolderStructureVisitor visitor, FolderContents contents) throws IOException;
	
}

