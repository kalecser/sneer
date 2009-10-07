package sneer.bricks.hardwaresharing.files.map.visitors.impl;

import java.io.IOException;

import sneer.bricks.hardwaresharing.files.map.visitors.FileMapGuide;
import sneer.bricks.hardwaresharing.files.map.visitors.FolderStructureVisitor;
import sneer.bricks.pulp.crypto.Sneer1024;

public class FileMapGuideImpl implements FileMapGuide {

	@Override
	public void guide(FolderStructureVisitor visitor, Sneer1024 startingPoint) throws IOException {
		new GuidedTour(startingPoint, visitor);
	}

}
