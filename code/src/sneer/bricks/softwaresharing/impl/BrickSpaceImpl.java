package sneer.bricks.softwaresharing.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.client.downloads.TimeoutException;
import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
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
import sneer.bricks.softwaresharing.publisher.BuildingHash;
import sneer.bricks.softwaresharing.publisher.BuildingPublisher;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.ClosureX;
import sneer.foundation.lang.Consumer;


class BrickSpaceImpl implements BrickSpace, Consumer<BuildingHash> {

	private final CacheMap<String, BrickHistory> _availableBricksByName = CacheMap.newInstance();

	private final EventNotifier<Seal> _newBuildingFound = my(EventNotifiers.class).newInstance();
	
	private BuildingHash _myOwnBuilding;

	@SuppressWarnings("unused")	private WeakContract _tupleSubscription;



	
	{
		my(Threads.class).startDaemon("BrickSpaceImpl init", new Closure() { @Override public void run() {
			init();
		}});
	}

	
	private void init() {
		my(TupleSpace.class).keep(BuildingHash.class);
		if (!tryToPublishMyOwnBuilding()) return;
		startReceivingBuildingsFromPeers();
	}

	
	@Override
	public Collection<BrickHistory> availableBricks() {
		return new ArrayList<BrickHistory>(_availableBricksByName.values());
	}

	
	@Override
	public void consume(final BuildingHash srcFolderHash) {
		my(Threads.class).startDaemon("BrickSpace Fetcher", new Closure() { @Override public void run() {
			fetchIfNecessary(srcFolderHash);
		}});
	}

	
	@Override
	public EventSource<Seal> newBuildingFound() {
		return _newBuildingFound.output();
	}
	
	
	synchronized
	private void fetchIfNecessary(final BuildingHash buildingHash) {
		shield("writing", new ClosureX<Exception>() { @Override public void run() throws Exception {
			if (!isAlreadyMapped(buildingHash))
				download(buildingHash);
			
			shield("reading", new ClosureX<Exception>() { @Override public void run() throws Exception {
				accumulateBricks(buildingHash, false);
			}});
		}});
	}


	private boolean isAlreadyMapped(final BuildingHash buildingHash) {
		return my(FileMap.class).getPath(buildingHash.value) != null;
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


	private void accumulateBricks(final BuildingHash buildingHash, boolean isMyOwn) throws IOException {
		my(Demolisher.class).demolishBuildingInto(
			_availableBricksByName,
			buildingHash.value,
			isMyOwn
		);

		_newBuildingFound.notifyReceivers(buildingHash.publisher);
	}


	private boolean tryToPublishMyOwnBuilding() {
		try {
			_myOwnBuilding = my(BuildingPublisher.class).publishMyOwnBuilding();
			accumulateBricks(_myOwnBuilding, true);
		} catch (IOException e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Error while reading bricks' code.", "", e);
			return false;
		}
		
		return true;
	}

	
	private void startReceivingBuildingsFromPeers() {
		_tupleSubscription = my(TupleSpace.class).addSubscription(BuildingHash.class, this);
	}


	private void download(final BuildingHash srcFolderHash) throws IOException, TimeoutException {
		File tmpFolderRoot = my(FolderConfig.class).tmpFolderFor(BrickSpace.class);
		File tmpFolder = new File(tmpFolderRoot, String.valueOf(System.nanoTime()));
		
		Download download = my(FileClient.class).startFolderDownload(tmpFolder, srcFolderHash.value);
		download.waitTillFinished();
	}

}
