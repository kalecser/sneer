package sneer.bricks.hardwaresharing.files.map.visitors;

import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.foundation.brickness.Brick;

@Brick
public interface FileMapGuide {

	void guide(FolderStructureVisitor visitor, Sneer1024 startingPoint);
	
}

