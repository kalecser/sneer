package dfcsantos.tracks.endorsements.client.downloads.downloader.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReference;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReferences;
import sneer.bricks.pulp.keymanager.ContactSeals;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.endorsements.client.downloads.counter.TrackDownloadCounter;
import dfcsantos.tracks.endorsements.client.downloads.downloader.TrackDownloader;
import dfcsantos.tracks.endorsements.protocol.TrackEndorsement;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;
import dfcsantos.tracks.storage.rejected.RejectedTracksKeeper;
import dfcsantos.wusic.Wusic;

class TrackDownloaderImpl implements TrackDownloader {

	private static final int CONCURRENT_DOWNLOADS_LIMIT = 3;

	private final ImmutableReference<Signal<Boolean>> _onOffSwitch = my(ImmutableReferences.class).newInstance();

	@SuppressWarnings("unused") private final WeakContract _trackEndorsementConsumerContract;

	{
		_trackEndorsementConsumerContract = my(TupleSpace.class).addSubscription(TrackEndorsement.class, new Consumer<TrackEndorsement>() { @Override public void consume(TrackEndorsement endorsement) {
			consumeTrackEndorsement(endorsement);
		}});
	}

	@Override
	public void setOnOffSwitch(Signal<Boolean> onOffSwitch) {
		_onOffSwitch.set(onOffSwitch);
	}

	private void consumeTrackEndorsement(TrackEndorsement endorsement) {
		if (!isOn()) return;

		if (hasReachedDownloadLimit()) return;

		if (my(ContactSeals.class).ownSeal().equals(endorsement.publisher)) return;
		
		if (isDuplicated(endorsement)) return;
		if (isRejected(endorsement)) return;
		if (hasSpentDownloadAllowance()) return;

		final Download download = my(FileClient.class).startFileDownload(fileToWrite(endorsement), endorsement.lastModified, endorsement.hash);

		download.finished().addPulseReceiver(new Runnable() { @Override public void run() {
			if (download.hasFinishedSuccessfully())
				my(TrackDownloadCounter.class).incrementer().run();		
		}});
	}

	private boolean isOn() {
		if (!_onOffSwitch.isAlreadySet()) return false;
		return _onOffSwitch.get().currentValue();
	}

	private static boolean hasReachedDownloadLimit() {
		return my(FileClient.class).numberOfRunningDownloads() >= CONCURRENT_DOWNLOADS_LIMIT;
	}

	private static boolean isRejected(TrackEndorsement endorsement) {
		return my(RejectedTracksKeeper.class).isRejected(endorsement.hash);
	}

	private static boolean isDuplicated(TrackEndorsement endorsement) {
		return my(FileMap.class).getFile(endorsement.hash) != null;
	}

	private static boolean hasSpentDownloadAllowance() {
		return peerTracksFolderSize() >= downloadAllowanceInBytes();
	}

	private static long peerTracksFolderSize() {
		return my(IO.class).files().sizeOfFolder(peerTracksFolder());
	}

	private static File peerTracksFolder() {
		return my(TracksFolderKeeper.class).peerTracksFolder();
	}

	private static int downloadAllowanceInBytes() {
		return 1024 * 1024 * my(Wusic.class).trackDownloadAllowance().currentValue();
	}

	private static File fileToWrite(TrackEndorsement endorsement) {
		String name = new File(endorsement.path).getName();
		return new File(peerTracksFolder(), name);
	}

}