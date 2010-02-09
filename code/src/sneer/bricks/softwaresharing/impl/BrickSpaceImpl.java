package sneer.bricks.softwaresharing.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.events.EventNotifier;
import sneer.bricks.pulp.events.EventNotifiers;
import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.pulp.keymanager.Seal;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.softwaresharing.BrickInfo;
import sneer.bricks.softwaresharing.BrickSpace;
import sneer.bricks.softwaresharing.demolisher.Demolisher;
import sneer.bricks.softwaresharing.publisher.SourcePublisher;
import sneer.bricks.softwaresharing.publisher.SrcFolderHash;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.ClosureX;
import sneer.foundation.lang.Consumer;


class BrickSpaceImpl implements BrickSpace, Consumer<SrcFolderHash> {

	private final CacheMap<String, BrickInfo> _availableBricksByName = CacheMap.newInstance();

	private final EventNotifier<Seal> _newBuildingFound = my(EventNotifiers.class).newInstance();
	
	@SuppressWarnings("unused")	private WeakContract _tupleSubscription;

	
	{
		my(Threads.class).startDaemon("BrickSpaceImpl init", new Closure() { @Override public void run() {
			//init();
		}});
	}

	
	@SuppressWarnings("unused")
	private void init() {
		my(TupleSpace.class).keep(SrcFolderHash.class);
		publishMySrcFolder();
		receiveSrcFoldersFromPeers();
	}

	
	@Override
	public Collection<BrickInfo> availableBricks() {
		return new ArrayList<BrickInfo>(_availableBricksByName.values());
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

	
	private void fetchIfNecessary(final SrcFolderHash srcFolderHash) {
		shield("writing", new ClosureX<IOException>() { @Override public void run() {

			my(FileClient.class).startFolderDownload(null, srcFolderHash.value);
		
			shield("reading", new ClosureX<IOException>() { @Override public void run() throws IOException {
				accumulateBricks(srcFolderHash);
			}});
		}});
	}

	
	private void shield(String operation, ClosureX<IOException> closure) {
		try {
			closure.run();
		} catch (IOException e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Error " + operation + " brick sources.", "This might indicate problems with your file device (Hard Drive). :(", e, 30000);
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
		return srcFolderHash.publisher.equals(my(Seals.class).ownSeal());
	}

	private void publishMySrcFolder() {
		my(SourcePublisher.class).publishSourceFolder();
	}

	private void receiveSrcFoldersFromPeers() {
		_tupleSubscription = my(TupleSpace.class).addSubscription(SrcFolderHash.class, this);
	}

}
