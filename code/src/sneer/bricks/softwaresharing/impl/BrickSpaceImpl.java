package sneer.bricks.softwaresharing.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.client.downloads.TimeoutException;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.events.EventNotifier;
import sneer.bricks.pulp.events.EventNotifiers;
import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.softwaresharing.BrickHistory;
import sneer.bricks.softwaresharing.BrickSpace;
import sneer.bricks.softwaresharing.demolisher.Demolisher;
import sneer.bricks.softwaresharing.publisher.SourcePublisher;
import sneer.bricks.softwaresharing.publisher.SrcFolderHash;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.ClosureX;
import sneer.foundation.lang.Consumer;


class BrickSpaceImpl implements BrickSpace, Consumer<SrcFolderHash> {

	private final CacheMap<String, BrickHistory> _availableBricksByName = CacheMap.newInstance();

	private final EventNotifier<Seal> _newBuildingFound = my(EventNotifiers.class).newInstance();
	
	@SuppressWarnings("unused")	private WeakContract _tupleSubscription;

	
	{
		my(Threads.class).startDaemon("BrickSpaceImpl init", new Closure() { @Override public void run() {
			init();
		}});
	}

	
	private void init() {
		my(TupleSpace.class).keep(SrcFolderHash.class);
		receiveSrcFoldersFromPeers();
		publishMySrcFolder();
	}

	
	@Override
	public Collection<BrickHistory> availableBricks() {
		return new ArrayList<BrickHistory>(_availableBricksByName.values());
	}

	
	@Override
	public void consume(final SrcFolderHash srcFolderHash) {
		my(Logger.class).log("Consuming SrcFolderHash");
		my(Threads.class).startDaemon("BrickSpace Fetcher", new Closure() { @Override public void run() {
			fetchIfNecessary(srcFolderHash);
		}});
	}

	
	@Override
	public EventSource<Seal> newBuildingFound() {
		return _newBuildingFound.output();
	}
	
	
	synchronized
	private void fetchIfNecessary(final SrcFolderHash srcFolderHash) {
		shield("writing", new ClosureX<Exception>() { @Override public void run() throws Exception {

			//if (!isMyOwn(srcFolderHash))
				download(srcFolderHash);
			
			shield("reading", new ClosureX<Exception>() { @Override public void run() throws Exception {
				accumulateBricks(srcFolderHash);
			}});
		}});
	}

	
	private void shield(String operation, ClosureX<Exception> closure) {
		try {
			closure.run();
		} catch (IOException e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Error " + operation + " brick sources.", "This might indicate problems with your file device (Hard Drive). :(", e, 30000);
		} catch (TimeoutException e) {
			my(BlinkingLights.class).turnOn(LightType.WARNING, "Timeout downloading brick sources.", null, e, 30000);
		} catch (Exception e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Error " + operation + " brick sources.", null, e, 30000);
		}
	}


	private void accumulateBricks(final SrcFolderHash srcFolderHash) throws IOException {
		my(Demolisher.class).demolishBuildingInto(
			_availableBricksByName,
			srcFolderHash.value,
			isMyOwn(srcFolderHash)
		);
		
		_newBuildingFound.notifyReceivers(srcFolderHash.publisher);
	}


	private boolean isMyOwn(SrcFolderHash srcFolderHash) {
		return srcFolderHash.publisher.equals(my(OwnSeal.class).get().currentValue());
	}

	
	private void publishMySrcFolder() {
		my(SourcePublisher.class).publishSourceFolder();
	}

	
	private void receiveSrcFoldersFromPeers() {
		_tupleSubscription = my(TupleSpace.class).addSubscription(SrcFolderHash.class, this);
	}


	private void download(final SrcFolderHash srcFolderHash) throws IOException, TimeoutException {
		File tmpFolderRoot = my(FolderConfig.class).tmpFolderFor(BrickSpace.class);
		File tmpFolder = new File(tmpFolderRoot, String.valueOf(System.nanoTime()));
		
		Download download = my(FileClient.class).startFolderDownload(tmpFolder, srcFolderHash.value);
		download.waitTillFinished();
	}

}
