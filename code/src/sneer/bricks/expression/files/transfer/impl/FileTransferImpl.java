package sneer.bricks.expression.files.transfer.impl;

import static basis.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.expression.files.map.mapper.MappingStopped;
import sneer.bricks.expression.files.server.FileServer;
import sneer.bricks.expression.files.transfer.FileTransfer;
import sneer.bricks.expression.files.transfer.FileTransferAccept;
import sneer.bricks.expression.files.transfer.FileTransferDetails;
import sneer.bricks.expression.files.transfer.FileTransferSugestion;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.notifiers.Notifier;
import sneer.bricks.pulp.notifiers.Notifiers;
import sneer.bricks.software.folderconfig.FolderConfig;
import basis.lang.Consumer;

public class FileTransferImpl implements FileTransfer {

	private final Notifier<FileTransferSugestion> _sugestionHandlers = my(Notifiers.class).newInstance();
	@SuppressWarnings("unused")
	private WeakContract ref, ref2, ref3;
	private Map<FileTransferSugestion, File> filesBySugestion = new LinkedHashMap<FileTransferSugestion, File>();

	{
		my(FileServer.class);
		ref = my(RemoteTuples.class).addSubscription(FileTransferSugestion.class, new Consumer<FileTransferSugestion>(){  @Override public void consume(FileTransferSugestion sugestion) {
			_sugestionHandlers.notifyReceivers(sugestion);
		}});
		
		ref2 = my(RemoteTuples.class).addSubscription(FileTransferAccept.class, new Consumer<FileTransferAccept>(){  @Override public void consume(FileTransferAccept accept) {
			handleAccept(accept);
		}});
		
		ref3 = my(RemoteTuples.class).addSubscription(FileTransferDetails.class, new Consumer<FileTransferDetails>(){  @Override public void consume(FileTransferDetails details) {
			handleDetails(details);
		}});
	}
	
	@Override
	public void tryToSend(File file, Seal peer) {
		FileTransferSugestion sugestion = new FileTransferSugestion(peer, file.getName(), file.lastModified());
		my(TupleSpace.class).add(sugestion);
		filesBySugestion.put(sugestion, file);
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
	}

	private void handleAccept(FileTransferAccept accept) {
		File file = getFile(accept);
		Hash hash = map(file);
		if (hash == null)
			return;
		my(TupleSpace.class).add(new FileTransferDetails(accept, hash));
	}
	
	private void handleDetails(FileTransferDetails details) {
		File tmp = my(FolderConfig.class).tmpFolder().get();
		File destination = new File(tmp, details.accept.sugestion.fileOrFolderName);
		my(FileClient.class).startFileDownload(
				destination, details.accept.sugestion.fileLastModified, details.hash, details.publisher);
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
}
