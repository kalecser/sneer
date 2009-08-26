package sneer.bricks.softwaresharing.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.Collection;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardwaresharing.files.client.FileClient;
import sneer.bricks.pulp.events.EventNotifier;
import sneer.bricks.pulp.events.EventNotifiers;
import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.softwaresharing.BrickInfo;
import sneer.bricks.softwaresharing.BrickSpace;
import sneer.bricks.softwaresharing.demolisher.Demolisher;
import sneer.bricks.softwaresharing.publisher.SourcePublisher;
import sneer.bricks.softwaresharing.publisher.SrcFolderHash;
import sneer.foundation.brickness.Seal;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Consumer;

class BrickSpaceImpl implements BrickSpace, Consumer<SrcFolderHash> {

	private final CacheMap<String, BrickInfo> _availableBricksByName = new CacheMap<String, BrickInfo>();

	private final EventNotifier<Seal> _newBuildingFound = my(EventNotifiers.class).newInstance();
	
	@SuppressWarnings("unused")	private final WeakContract _tupleSubscription;

	
	{
		my(TupleSpace.class).keep(SrcFolderHash.class);
		_tupleSubscription = my(TupleSpace.class).addSubscription(SrcFolderHash.class, this);
		
		my(SourcePublisher.class).publishSourceFolder();
	}
	
	
	@Override
	public Collection<BrickInfo> availableBricks() {
		return new ArrayList<BrickInfo>(_availableBricksByName.values());
	}

	
	@Override
	public void consume(final SrcFolderHash srcFolderHash) {
		my(Threads.class).startDaemon("BrickSpace Fetcher", new Runnable() { @Override public void run() {
			fetchIfNecessary(srcFolderHash);
		}});
	}

	
	@Override
	public EventSource<Seal> newBuildingFound() {
		return _newBuildingFound.output();
	}

	
	private void fetchIfNecessary(final SrcFolderHash srcFolderHash) {
		my(Logger.class).log("Fetching: " + srcFolderHash);
		my(FileClient.class).fetchToCache(srcFolderHash.value);
		my(Logger.class).log("Fetched: " + srcFolderHash);
		my(Logger.class).log("FileClient: if already fetching join that thread. Keep set of all hashes being fetched");

		accumulateBricks(srcFolderHash);
	}

	
	private void accumulateBricks(final SrcFolderHash srcFolderHash) {
		my(Demolisher.class).demolishBuilding(
			_availableBricksByName,
			srcFolderHash.value,
			isCurrent(srcFolderHash)
		);
		
		_newBuildingFound.notifyReceivers(srcFolderHash.publisher());
	}


	private boolean isCurrent(SrcFolderHash srcFolderHash) {
		return srcFolderHash.publisher().equals(my(Seals.class).ownSeal());
	}

}
