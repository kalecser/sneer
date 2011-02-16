package sneer.bricks.hardwaresharing.backup.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.expression.files.map.mapper.MappingStopped;
import sneer.bricks.expression.files.protocol.FileOrFolder;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.IO.Files;
import sneer.bricks.hardwaresharing.backup.FileEvent;
import sneer.bricks.hardwaresharing.backup.FileToRestore;
import sneer.bricks.hardwaresharing.backup.FileToSync;
import sneer.bricks.hardwaresharing.backup.FolderToSync;
import sneer.bricks.hardwaresharing.backup.HardDriveMegabytesLent;
import sneer.bricks.hardwaresharing.backup.RestoreRequest;
import sneer.bricks.hardwaresharing.backup.Snackup;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.exceptions.Refusal;

import com.sun.xml.internal.txw2.IllegalSignatureException;

class SnackupImpl implements Snackup {
	
	private static final int FIVE_MINUTES = 1000 * 60 * 5;
	
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGc;
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGc2;
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGc3;
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGc4;

	{
		my(Attributes.class).registerAttribute(HardDriveMegabytesLent.class);
		my(Attributes.class).registerAttribute(FolderToSync.class);
		
		_refToAvoidGc = my(Attributes.class).myAttributeValue(FolderToSync.class).addReceiver(new Consumer<String>() {  @Override public void consume(String value) {
			handleFolderToSyncChange();
		}});
		
		_refToAvoidGc2= my(Timer.class).wakeUpNowAndEvery(FIVE_MINUTES, new Runnable() { @Override public void run() {
			updateFolderToSync();
		}});
		
		_refToAvoidGc3 = my(RemoteTuples.class).addSubscription(FileToSync.class, new Consumer<FileToSync>() {  @Override public void consume(FileToSync value) {
			handleFileToSync(value);
		}});
		
		_refToAvoidGc4 = my(RemoteTuples.class).addSubscription(RestoreRequest.class, new Consumer<RestoreRequest>() {  @Override public void consume(RestoreRequest value) {
			handleRestoreRequest(value);
		}});
	}
	
	@Override
	public void sync() {
		updateFolderToSync();
	}
	
	protected void handleFolderToSyncChange() {
		my(TupleSpace.class).add(new RestoreRequest());
	}

	protected void handleRestoreRequest(RestoreRequest value) {
		FileMap fileMap = my(FileMap.class);
		String lentFolder = lentFolderFor(value.publisher).getAbsolutePath();
		for (FileOrFolder entry : fileMap.dir(lentFolder))
			my(TupleSpace.class).add(new FileToRestore(entry.hashOfContents, entry.lastModified, relativeNameWithin(lentFolder, entry.name))); 
	}

	protected void handleFileToSync(FileEvent value) {
		File file;
		try {
			file = fileFor(value);
		} catch (IOException e) {
			turnOnBlinkingLightFor(e, "Error creating local file for download");
			return;
		}
		my(FileClient.class).startFileDownload(file, value.lastModified, value.hash, value.publisher);
	}

	private File fileFor(FileEvent value) throws IOException {
		File result = new File(lentFolderFor(value.publisher), value.relativePath);
		files().forceMkdir(result.getParentFile());
		return result;
	}

	private File lentFolderFor(Seal publisher) {
		return new File(tmpFolder(), publisher.toString());
	}

	private File tmpFolder() {
		return my(FolderConfig.class).tmpFolderFor(Snackup.class);
	}

	protected void updateFolderToSync() {
		String folder = folderToSync();
		if (folder == null) return;
		
		Iterator<File> files = files().iterate(new File(folder), null, true);
		while (files.hasNext()) {
			File file = files.next();
			updateFileToSync(file, relativeNameWithin(folder, file));
		}
	}

	private Files files() {
		return my(IO.class).files();
	}
	
	private String relativeNameWithin(String folder, File file) {
		return relativeNameWithin(folder, file.getAbsolutePath());
	}

	private String relativeNameWithin(String folder, String fileName) {
		return fileName.substring(folder.length() + 1);
	}

	private void updateFileToSync(File file, String relativeName) {
		Hash hash;
		try {
			hash = my(FileMapper.class).mapFileOrFolder(file);
		} catch (MappingStopped e) {
			throw new IllegalSignatureException(e);
		} catch (IOException e) {
			turnOnBlinkingLightFor(e, "Error reading file");
			return;
		}
		my(TupleSpace.class).add(new FileToSync(hash, file.lastModified(), relativeName));
	}

	private void turnOnBlinkingLightFor(IOException e, String caption) {
		my(BlinkingLights.class).turnOn(LightType.ERROR, "Snackup - " + caption, e.getMessage(), e);
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
