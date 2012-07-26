package sneer.bricks.softwaresharing.gui.impl;

import javax.swing.AbstractListModel;

import sneer.bricks.softwaresharing.BrickVersion;
import sneer.bricks.softwaresharing.FileVersion;

public class FileVersionListModel extends AbstractListModel<FileVersionWrapper> {

	private final BrickVersion _brickVersion;

	
	FileVersionListModel(BrickVersion brickVersion){
		_brickVersion = brickVersion;
	}
	
	
	@Override public FileVersionWrapper getElementAt(int index) {
		return wrap(_brickVersion.files().get(index));
	}

	
	@Override public int getSize() {
		return _brickVersion.files().size();
	}

	
	private FileVersionWrapper wrap(FileVersion fileVersion){
		return new FileVersionWrapper(fileVersion);
	}
}
