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
import sneer.foundation.lang.Predicate;


class BrickSpaceImpl implements BrickSpace, Consumer<SrcFolderHash> {

	private final CacheMap<String, BrickInfo> _availableBricksByName = new CacheMap<String, BrickInfo>();

	private final EventNotifier<Seal> _newBuildingFound = my(EventNotifiers.class).newInstance();
	
	@SuppressWarnings("unused")	private WeakContract _tupleSubscription;

	
	{
		my(Threads.class).startDaemon("BrickSpaceImpl init", new Runnable() { @Override public void run() {
			init();
		}});
	}

	
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
		my(Threads.class).startDaemon("BrickSpace Fetcher", new Runnable() { @Override public void run() {
			fetchIfNecessary(srcFolderHash);
		}});
	}

	
	@Override
	public EventSource<Seal> newBuildingFound() {
		return _newBuildingFound.output();
	}

	
	private void fetchIfNecessary(final SrcFolderHash srcFolderHash) {
		my(FileClient.class).fetchToCache(srcFolderHash.value);
		accumulateBricks(srcFolderHash);
	}

	
	private void accumulateBricks(final SrcFolderHash srcFolderHash) {
		my(Demolisher.class).demolishBuildingInto(
			_availableBricksByName,
			srcFolderHash.value,
			isMyOwn(srcFolderHash)
		);
		
		_newBuildingFound.notifyReceivers(srcFolderHash.publisher());
	}


	private boolean isMyOwn(SrcFolderHash srcFolderHash) {
		return srcFolderHash.publisher().equals(my(Seals.class).ownSeal());
	}

	private void publishMySrcFolder() {
		my(SourcePublisher.class).publishSourceFolder();
	}

	private void receiveSrcFoldersFromPeers() {
		_tupleSubscription = my(TupleSpace.class).addSubscription(SrcFolderHash.class, this, new Predicate<SrcFolderHash>() {
			@Override
			public boolean evaluate(SrcFolderHash tuple) {
				return true;
			}
		});
	}

}
