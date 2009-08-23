package sneer.bricks.softwaresharing.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardwaresharing.files.client.FileClient;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.events.EventNotifier;
import sneer.bricks.pulp.events.EventNotifiers;
import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.softwaresharing.BrickInfo;
import sneer.bricks.softwaresharing.BrickSpace;
import sneer.bricks.softwaresharing.filetobrick.FileToBrickConverter;
import sneer.bricks.softwaresharing.publisher.BrickPublisher;
import sneer.bricks.softwaresharing.publisher.SrcFolderHash;
import sneer.foundation.brickness.Seal;
import sneer.foundation.lang.Consumer;

class BrickSpaceImpl implements BrickSpace, Consumer<SrcFolderHash> {

	private final Map<Seal, Sneer1024> _cachedSrcFolderHashesByPeer = new ConcurrentHashMap<Seal, Sneer1024>();
	private final EventNotifier<Seal> _newBrickConfigurationFound = my(EventNotifiers.class).newInstance();
	
	@SuppressWarnings("unused")	private final WeakContract _brickUsageContract;

	
	{
		my(TupleSpace.class).keep(SrcFolderHash.class);
		_brickUsageContract = my(TupleSpace.class).addSubscription(SrcFolderHash.class, this);
		
		my(BrickPublisher.class).publishAllBricks();
	}
	
	
	@Override
	public Collection<BrickInfo> availableBricks() {
		return my(FileToBrickConverter.class).bricksInCachedSrcFolders(cachedSrcFolders());
	}

	private List<Sneer1024> cachedSrcFolders() {
		return new ArrayList<Sneer1024>(_cachedSrcFolderHashesByPeer.values());
	}

	@Override
	public void consume(final SrcFolderHash srcFolderHash) {
		my(Threads.class).startDaemon("BrickSpace Fetcher", new Runnable() { @Override public void run() {
			fetch(srcFolderHash);
		}});
	}

	@Override
	public EventSource<Seal> newBrickConfigurationFound() {
		return _newBrickConfigurationFound.output();
	}

	private void fetch(final SrcFolderHash srcFolderHash) {
		my(FileClient.class).fetchToCache(srcFolderHash.value);
		my(Logger.class).log("FileClient: if already fetching join that thread. Keep set of all hashes being fetched");

		markAsCached(srcFolderHash);
	}

	synchronized
	private void markAsCached(final SrcFolderHash srcFolderHash) {
		List<Sneer1024> previouslyCachedSrcFolders = cachedSrcFolders();

		Seal publisher = srcFolderHash.publisher();
		_cachedSrcFolderHashesByPeer.put(publisher, srcFolderHash.value);

		if (!previouslyCachedSrcFolders.contains(srcFolderHash.value))
			_newBrickConfigurationFound.notifyReceivers(publisher);
	}

}
