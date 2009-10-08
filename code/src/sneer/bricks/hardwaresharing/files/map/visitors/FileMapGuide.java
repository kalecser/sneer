package sneer.bricks.hardwaresharing.files.map.visitors;

import java.io.IOException;

import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.foundation.brickness.Brick;

@Brick
public interface FileMapGuide {

	void guide(FolderStructureVisitor visitor, FolderContents contents) throws IOException;
	
}

