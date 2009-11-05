package sneer.bricks.hardwaresharing.files.map.visitors.impl;

import java.io.IOException;

import sneer.bricks.hardwaresharing.files.map.visitors.FileMapGuide;
import sneer.bricks.hardwaresharing.files.map.visitors.FolderStructureVisitor;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;

public class FileMapGuideImpl implements FileMapGuide {

	@Override
	public void guide(FolderStructureVisitor visitor, FolderContents contents) throws IOException {
		new GuidedTour(visitor, contents);
	}

}
