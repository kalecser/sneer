package sneer.bricks.hardwaresharing.backup.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.Iterator;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardwaresharing.backup.FolderToBeBackedUp;
import sneer.bricks.hardwaresharing.backup.Snackup;
import sneer.bricks.network.social.attributes.Attributes;

class SnackupImpl implements Snackup {
	
	private static final int FIVE_MINUTES = 1000 * 60 * 5;
	
	
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGc;

	{
		_refToAvoidGc = my(Timer.class).wakeUpNowAndEvery(FIVE_MINUTES, new Runnable() { @Override public void run() {
			updateFolderToBeBackedUp();
		}});
	}
	
	
	@Override
	public void sync() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	
	protected void updateFolderToBeBackedUp() {
		String folder = folderToBeBackedUp();
		if (folder == null) return;
		
		Iterator<File> files = my(IO.class).files().iterate(new File(folder), null, true);
		while (files.hasNext())
			updateFileToBeBackedUp(files.next());
	}

	
	private void updateFileToBeBackedUp(@SuppressWarnings("unused") File file) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	private String folderToBeBackedUp() {
		return my(Attributes.class).myAttributeValue(FolderToBeBackedUp.class).currentValue();
	}

}
