package sneer.bricks.softwaresharing.impl;

import static basis.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import basis.lang.CacheMap;
import basis.lang.Closure;
import basis.lang.ClosureX;
import basis.lang.Consumer;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.client.downloads.TimeoutException;
import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.notifiers.Notifier;
import sneer.bricks.pulp.notifiers.Notifiers;
import sneer.bricks.pulp.notifiers.Source;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.softwaresharing.BrickHistory;
import sneer.bricks.softwaresharing.BrickSpace;
import sneer.bricks.softwaresharing.demolisher.Demolisher;
import sneer.bricks.softwaresharing.publisher.BuildingHash;
import sneer.bricks.softwaresharing.publisher.BuildingPublisher;


class BrickSpaceImpl implements BrickSpace, Consumer<BuildingHash> {

	private CacheMap<String, BrickHistory> _availableBricksByName;

	private final Notifier<Seal> _newBuildingFound = my(Notifiers.class).newInstance();
	
	private BuildingHash _myOwnBuilding;

	@SuppressWarnings("unused")	private WeakContract _tupleSubscription;

	
	{
		my(Threads.class).startDaemon("BrickSpaceImpl init", new Closure() { @Override public void run() {
			if ("true".equals(System.getProperty("sneer.testmode")))
				init(); //Not yet ready for production. Maps src files over and over again because every new build comes with all lastModified dates changed because git does not preserve lastModified dates.
		}});
	}

	
	private void init() {
		my(TupleSpace.class).keep(BuildingHash.class);
		if (!tryToPublishMyOwnBuilding()) return;
		startReceivingBuildingsFromPeers();
	}

	
	@Override
	public Collection<BrickHistory> availableBricks() {
		if (_availableBricksByName == null)
			return Collections.emptyList();
		return new ArrayList<BrickHistory>(_availableBricksByName.values());
	}

	
	@Override
	public void consume(final BuildingHash srcFolderHash) {
		final Contact publisher = my(ContactSeals.class).contactGiven(srcFolderHash.publisher);
		if (publisher == null)
			return;
		my(Threads.class).startDaemon("BrickSpace Fetcher", new Closure() { @Override public void run() {
			fetchIfNecessary(srcFolderHash, publisher);
		}});
	}

	
	@Override
	public Source<Seal> newBuildingFound() {
		return _newBuildingFound.output();
	}
	
	
	private void fetchIfNecessary(final BuildingHash buildingHash, final Contact publisher) {
		shield("writing", new ClosureX<Exception>() { @Override public void run() throws Exception {
			if (!isAlreadyMapped(buildingHash))
				download(buildingHash);
			
			shield("reading", new ClosureX<Exception>() { @Override public void run() throws Exception {
				accumulateBricks(buildingHash, publisher);
			}});
		}});
	}


	private boolean isAlreadyMapped(final BuildingHash buildingHash) {
		return my(FileMap.class).getFolder(buildingHash.value) != null;
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


	private void accumulateBricks(final BuildingHash buildingHash, Contact publisher) throws IOException {
		
		my(Demolisher.class).demolishBuildingInto(
			_availableBricksByName,
			buildingHash.value,
			publisher
		);

		_newBuildingFound.notifyReceivers(buildingHash.publisher);
	}


	private boolean tryToPublishMyOwnBuilding() {
		try {
			_myOwnBuilding = my(BuildingPublisher.class).publishMyOwnBuilding();
			_availableBricksByName = my(Demolisher.class).demolishOwnBuilding(_myOwnBuilding.value);
			_newBuildingFound.notifyReceivers(_myOwnBuilding.publisher);
		} catch (IOException e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Error while reading bricks' code.", "", e);
			return false;
		}
		return true;
	}

	
	private void startReceivingBuildingsFromPeers() {
		_tupleSubscription = my(RemoteTuples.class).addSubscription(BuildingHash.class, this);
	}


	private void download(final BuildingHash srcFolderHash) throws IOException, TimeoutException {
		File tmpFolderRoot = my(FolderConfig.class).tmpFolderFor(BrickSpace.class);
		File tmpFolder = new File(tmpFolderRoot, String.valueOf(System.nanoTime()));
		
		Download download = my(FileClient.class).startFolderNoveltiesDownload(tmpFolder, srcFolderHash.value);
		download.waitTillFinished();
	}

}
