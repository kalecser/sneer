package sneer.bricks.hardwaresharing.files.map.visitors.impl;

import sneer.bricks.hardwaresharing.files.map.visitors.FileMapGuide;
import sneer.bricks.hardwaresharing.files.map.visitors.FolderStructureVisitor;
import sneer.bricks.pulp.crypto.Sneer1024;

public class FileMapGuideImpl implements FileMapGuide {

	@Override
	public void guide(FolderStructureVisitor visitor, Sneer1024 startingPoint) {
		new GuidedTour(startingPoint, visitor);
	}

}
