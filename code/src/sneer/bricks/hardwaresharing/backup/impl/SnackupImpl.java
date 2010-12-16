package sneer.bricks.hardwaresharing.backup.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.Iterator;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardwaresharing.backup.FolderToSync;
import sneer.bricks.hardwaresharing.backup.HardDriveMegabytesLent;
import sneer.bricks.hardwaresharing.backup.Snackup;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.exceptions.Refusal;

class SnackupImpl implements Snackup {
	
	private static final int FIVE_MINUTES = 1000 * 60 * 5;
	
	
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGc;

	{
		_refToAvoidGc = my(Timer.class).wakeUpNowAndEvery(FIVE_MINUTES, new Runnable() { @Override public void run() {
			updateFolderToSync();
		}});
	}
	
	
	@Override
	public void sync() {
		updateFolderToSync();
	}

	
	protected void updateFolderToSync() {
		String folder = folderToSync();
		if (folder == null) return;
		
		Iterator<File> files = my(IO.class).files().iterate(new File(folder), null, true);
		while (files.hasNext())
			updateFileToSync(files.next());
	}

	
	private void updateFileToSync(@SuppressWarnings("unused") File file) {
		//my(TupleSpace.class).acquire(new FileToSync(relativeName(file)));
	}

	private String folderToSync() {
		return my(Attributes.class).myAttributeValue(FolderToSync.class).currentValue();
	}


	@Override
	public void lendSpaceTo(Contact contact, int megaBytes) throws Refusal {
		my(Attributes.class).attributeSetterFor(contact, HardDriveMegabytesLent.class).consume(megaBytes);
	}


	@Override
	public Consumer<String> folderToSyncSetter() {
		return my(Attributes.class).myAttributeSetter(FolderToSync.class);
	}

}
