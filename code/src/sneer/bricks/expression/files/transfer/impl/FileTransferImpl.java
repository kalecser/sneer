package sneer.bricks.expression.files.transfer.impl;

import static basis.environments.Environments.my;
import static sneer.bricks.pulp.blinkinglights.LightType.INFO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.expression.files.map.mapper.MappingStopped;
import sneer.bricks.expression.files.server.FileServer;
import sneer.bricks.expression.files.transfer.FileTransfer;
import sneer.bricks.expression.files.transfer.FileTransferAccept;
import sneer.bricks.expression.files.transfer.FileTransferDetails;
import sneer.bricks.expression.files.transfer.FileTransferSugestion;
import sneer.bricks.expression.files.transfer.downloadfolder.DownloadFolder;
import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.notifiers.Notifier;
import sneer.bricks.pulp.notifiers.Notifiers;
import basis.lang.Closure;
import basis.lang.Consumer;
import basis.lang.Predicate;

public class FileTransferImpl implements FileTransfer {

	private static final int THREE_DAYS = 1000 * 60 * 60 * 24 * 3;
	
	private final Notifier<FileTransferSugestion> _sugestionHandlers = my(Notifiers.class).newInstance();
	@SuppressWarnings("unused")
	private final Object ref1, ref2, ref3;
	private final Collection<Object> refs = new ArrayList<Object>();

	private final Map<FileTransferSugestion, File> filesBySugestion = new ConcurrentHashMap<FileTransferSugestion, File>();
	private final Map<FileTransferSugestion, Light> waitingLightsBySuggestion = new ConcurrentHashMap<FileTransferSugestion, Light>();

	
	{
		my(TupleSpace.class).keepChosen(FileTransferSugestion.class, new Predicate<FileTransferSugestion>() {  @Override public boolean evaluate(FileTransferSugestion sug) {
			return isRecent(sug);
		}});
		
		ref1 = my(RemoteTuples.class).addSubscription(FileTransferSugestion.class, new Consumer<FileTransferSugestion>(){  @Override public void consume(FileTransferSugestion sugestion) {
			_sugestionHandlers.notifyReceivers(sugestion);
		}});
		
		ref2 = my(RemoteTuples.class).addSubscription(FileTransferAccept.class, new Consumer<FileTransferAccept>(){  @Override public void consume(FileTransferAccept accept) {
			handleAccept(accept);
		}});
		
		ref3 = my(RemoteTuples.class).addSubscription(FileTransferDetails.class, new Consumer<FileTransferDetails>(){  @Override public void consume(FileTransferDetails details) {
			handleDetails(details);
		}});

		my(FileServer.class);
	}
	
	
	@Override
	public void tryToSend(File fileOrFolder, Seal peer) {
		FileTransferSugestion sugestion = new FileTransferSugestion(peer, fileOrFolder.getName(), fileOrFolder.isDirectory(), fileOrFolder.lastModified());
		my(TupleSpace.class).add(sugestion);
		filesBySugestion.put(sugestion, fileOrFolder);
	}

	
	protected File getFile(FileTransferAccept accept) {
		return filesBySugestion.get(accept.sugestion);
	}

	
	@Override
	public WeakContract registerHandler(Consumer<FileTransferSugestion> sugestionHandler) {
		return _sugestionHandlers.output().addReceiver(sugestionHandler);
	}

	
	@Override
	public void accept(FileTransferSugestion sugestion) {
		my(TupleSpace.class).add(new FileTransferAccept(sugestion));
		turnOnWaitingLight(sugestion);
	}


	private void handleAccept(FileTransferAccept accept) {
		if (!isValid(accept)) return;
		File file = getFile(accept);
		Hash hash = map(file);
		if (hash == null)
			return;
		my(TupleSpace.class).add(new FileTransferDetails(accept, hash));
	}
	
	
	private boolean isValid(FileTransferAccept accept) {
		Tuple sugestion = accept.sugestion;
		if (!sugestion.publisher.equals(my(OwnSeal.class).get().currentValue())) return false;
		if (!sugestion.addressee.equals(accept.publisher)) return false;
		
		return my(TupleSpace.class).contains(sugestion);
	}


	private void handleDetails(FileTransferDetails details) {
		FileTransferSugestion sugestion = details.accept.sugestion;
		turnOffWaitingLight(sugestion);
		
		File destination = new File(downloadFolder(), sugestion.fileOrFolderName);
		Download download = my(FileClient.class).startDownload(
			destination, sugestion.isFolder, sugestion.fileLastModified, details.hash, details.publisher);
		
		startDisplayingProgress(download);
	}


	private File downloadFolder() {
		String path = my(Attributes.class).myAttributeValue(DownloadFolder.class).currentValue();
		if (path == null) throw new IllegalStateException("DownloadFolder must be set.");
		File ret = new File(path);
		if (!ret.isDirectory()) throw new IllegalStateException("DownloadFolder is not a valid directory: " + path);
		return ret;
	}

	
	private void turnOnWaitingLight(FileTransferSugestion sugestion) {
		Light light = my(BlinkingLights.class).turnOn(LightType.INFO, "Waiting for " + sugestion.fileOrFolderName, "The transfer is being prepared at the sending side...");
		waitingLightsBySuggestion.put(sugestion, light);
	}

	
	private void turnOffWaitingLight(FileTransferSugestion sugestion) {
		Light light = waitingLightsBySuggestion.remove(sugestion);
		if (light != null)
			turnOff(light);
	}

	
	private void startDisplayingProgress(final Download download) {
		final Light progressLight = my(BlinkingLights.class).prepare(INFO);

		final Object progressContract = download.progress().addReceiver(new Consumer<Integer>() {  @Override public void consume(Integer value) {
			turnOff(progressLight);
			my(BlinkingLights.class).turnOnIfNecessary(progressLight, value + "% " + download.file().getName(), "Download in progress:\n\n " + download.file().getAbsolutePath());
		}});
		refs.add(progressContract);
		
		download.onFinished(new Closure() { @Override public void run() {
			turnOff(progressLight);
			my(BlinkingLights.class).turnOn(LightType.GOOD_NEWS, download.file().getName() + " downloaded!", download.file().getAbsolutePath(), 10000);
			refs.remove(download);
			refs.remove(progressContract);
		}});
	}

	private void turnOff(final Light light) {
		my(BlinkingLights.class).turnOffIfNecessary(light);
	}
	
	
	private Hash map(File file) {
		try {
			return my(FileMapper.class).mapFileOrFolder(file);
		} catch (MappingStopped mse) {
			//ignored
		} catch (IOException ioe) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Error reading " + file, "This might indicate a problem with your harddrive", ioe);
		}
		
		return null;
	}
	

	private boolean isRecent(FileTransferSugestion sug) {
		return (my(Clock.class).time().currentValue() - sug.publicationTime) < THREE_DAYS;
	}

}
