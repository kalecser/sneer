package sneer.bricks.hardwaresharing.backup.impl;

import static basis.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import basis.lang.Closure;
import basis.lang.Consumer;
import basis.lang.exceptions.Refusal;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.expression.files.map.mapper.MappingStopped;
import sneer.bricks.expression.files.protocol.FileOrFolder;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.codec.Codec;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.IO.Files;
import sneer.bricks.hardwaresharing.backup.FileEvent;
import sneer.bricks.hardwaresharing.backup.FileToRestore;
import sneer.bricks.hardwaresharing.backup.FileToSync;
import sneer.bricks.hardwaresharing.backup.ImportantFolder;
import sneer.bricks.hardwaresharing.backup.InSync;
import sneer.bricks.hardwaresharing.backup.RestoreRequest;
import sneer.bricks.hardwaresharing.backup.Snackup;
import sneer.bricks.hardwaresharing.backup.kernel.SnackupKernel;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.attributes.Attribute;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.FolderConfig;

class SnackupImpl implements Snackup {
	
	private static final int FIVE_MINUTES = 1000 * 60 * 5;
	
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGc;
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGc2;
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGc3;
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGc4;
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGc5;
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGc6;

	private final Register<Boolean> _isSynced = my(Signals.class).newRegister(false);

	{
		my(Attributes.class).registerAttribute(ImportantFolder.class);
		
		_refToAvoidGc = my(Attributes.class).myAttributeValue(ImportantFolder.class).addReceiver(new Consumer<String>() {  @Override public void consume(String value) {
			handleFolderToSyncChange();
		}});
		
		_refToAvoidGc2= my(Timer.class).wakeUpNowAndEvery(FIVE_MINUTES, new Runnable() { @Override public void run() {
			updateFolderToSync();
		}});
		
		_refToAvoidGc3 = my(RemoteTuples.class).addSubscription(FileToSync.class, new Consumer<FileToSync>() {  @Override public void consume(final FileToSync value) {
			handleFileToSync(value);
		}});
		
		_refToAvoidGc4 = my(RemoteTuples.class).addSubscription(RestoreRequest.class, new Consumer<RestoreRequest>() {  @Override public void consume(RestoreRequest value) {
			handleRestoreRequest(value);
		}});
		
		_refToAvoidGc5 = my(RemoteTuples.class).addSubscription(FileToRestore.class, new Consumer<FileToRestore>() {  @Override public void consume(FileToRestore value) {
			handleFileToRestore(value);
		}});
		
		_refToAvoidGc6 = my(RemoteTuples.class).addSubscription(InSync.class, new Consumer<InSync>() {  @Override public void consume(InSync value) {
			setIsSync(true);
		}});
	}
	
	@Override
	public Signal<Boolean> isSynced() {
		return _isSynced.output();
	}
	
	protected void handleFileToRestore(final FileToRestore value) {
		tryToDownload(value, new File(importantFolder()));
	}
	
	protected void handleFolderToSyncChange() {
		setIsSync(false);
		updateFolderToSync();
		my(TupleSpace.class).add(new RestoreRequest());
	}

	private void setIsSync(boolean value) {
		_isSynced.setter().consume(value);
	}

	protected void handleRestoreRequest(RestoreRequest value) {
		FileMap fileMap = my(FileMap.class);
		String lentFolder = lentFolderFor(value.publisher).getAbsolutePath();
		for (FileOrFolder entry : fileMap.dir(lentFolder))
			my(TupleSpace.class).add(new FileToRestore(entry.hashOfContents, entry.size, entry.lastModified, entry.name));
	}

	protected void tryToDownload(final FileEvent value, File folder) {
		final File file;
		try {
			file = fileFor(value, folder);
		} catch (IOException e) {
			turnOnBlinkingLightFor(e, "Error creating local file for download");
			return;
		}
		
		final Download download = my(FileClient.class).startFileDownload(file, value.size, value.lastModified, value.hash, value.publisher);
		download.onFinished(new Closure() {  @Override public void run() {
			if (!download.hasFinishedSuccessfully())
				return;
			
			onFileDownload(value);
		}});
	}

	private File fileFor(FileEvent value, File parentFolder) throws IOException {
		File result = new File(parentFolder, value.relativePath);
		files().forceMkdir(result.getParentFile());
		return result;
	}

	private File lentFolderFor(Seal publisher) {
		return new File(tmpFolder(), my(Codec.class).hex().encode(publisher.bytes.copy()));
	}

	private File tmpFolder() {
		return my(FolderConfig.class).tmpFolderFor(Snackup.class);
	}

	protected void updateFolderToSync() {
		String folder = importantFolder();
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
			throw new IllegalStateException(e); //IllegalSignatureException broke the build.
		} catch (IOException e) {
			turnOnBlinkingLightFor(e, "Error reading file");
			return;
		}
		my(TupleSpace.class).add(new FileToSync(hash, file.length(), file.lastModified(), relativeName));
	}

	private void turnOnBlinkingLightFor(Exception e, String caption) {
		my(BlinkingLights.class).turnOn(LightType.ERROR, "Snackup - " + caption, e.getMessage(), e);
	}

	private String importantFolder() {
		return folderToSync().currentValue();
	}

	@Override
	public void lendSpaceTo(Contact contact, int megaBytes) throws Refusal {
		my(SnackupKernel.class).lendSpaceTo(contact, megaBytes);
	}

	@Override
	public Consumer<String> folderToSyncSetter() {
		return my(Attributes.class).myAttributeSetter(ImportantFolder.class);
	}

	private void onFileDownload(FileEvent value) {
		my(TupleSpace.class).add(new InSync(value.publisher));
		setIsSync(true);
	}

	private void handleFileToSync(final FileToSync value) {
		tryToDownload(value, lentFolderFor(value.publisher));
	}

	@Override
	public Signal<String> folderToSync() {
		return myAttributeValue(ImportantFolder.class);
	}

	private <T> Signal<T> myAttributeValue(Class<? extends Attribute<T>> attribute) {
		return my(Attributes.class).myAttributeValue(attribute);
	}

}
