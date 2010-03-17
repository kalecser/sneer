package dfcsantos.tracks.endorsements.client.downloads.downloader.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.hardware.cpu.crypto.Sneer1024;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReference;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReferences;
import sneer.bricks.hardware.ram.ref.weak.keeper.WeakReferenceKeeper;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.heartbeat.stethoscope.Stethoscope;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.assessment.tastematcher.MusicalTasteMatcher;
import dfcsantos.tracks.endorsements.client.downloads.counter.TrackDownloadCounter;
import dfcsantos.tracks.endorsements.client.downloads.downloader.TrackDownloader;
import dfcsantos.tracks.endorsements.protocol.TrackEndorsement;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;
import dfcsantos.tracks.storage.rejected.RejectedTracksKeeper;

class TrackDownloaderImpl implements TrackDownloader {

	private static final int CONCURRENT_DOWNLOADS_LIMIT = 3;

	private final ImmutableReference<Signal<Boolean>> _onOffSwitch = my(ImmutableReferences.class).newInstance();

	private final ImmutableReference<Signal<Integer>> _downloadAllowance = my(ImmutableReferences.class).newInstance();

	private final Set<Sneer1024> _tracksBeingDownloaded = new HashSet<Sneer1024>(); 

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

	@Override
	public void setTrackDownloadAllowance(Signal<Integer> downloadAllowance) {
		_downloadAllowance.set(downloadAllowance);
	}

	private void consumeTrackEndorsement(final TrackEndorsement endorsement) {
		if (!isOn()) return;

		if (hasReachedDownloadLimit()) return;

		if (my(OwnSeal.class).get().equals(endorsement.publisher)) return;

		if (isDuplicated(endorsement)) return;
		if (isRejected(endorsement)) return;
		if (hasSpentDownloadAllowance()) return;

		if (!isFromTheBestAvailableSource(endorsement)) return;

		final Download download = my(FileClient.class).startFileDownload(fileToWrite(endorsement), endorsement.lastModified, endorsement.hash);
		_tracksBeingDownloaded.add(endorsement.hash);

		WeakContract weakContract = download.finished().addPulseReceiver(new Runnable() { @Override public void run() {
			my(TrackDownloadCounter.class).conditionalIncrementer(download.hasFinishedSuccessfully()).run();
			_tracksBeingDownloaded.remove(endorsement.hash);
		}});

		my(WeakReferenceKeeper.class).keep(download, weakContract);
	}

	private boolean isOn() {
		if (!_onOffSwitch.isAlreadySet()) return false;
		return _onOffSwitch.get().currentValue();
	}

	private boolean hasReachedDownloadLimit() {
		return _tracksBeingDownloaded.size() >= CONCURRENT_DOWNLOADS_LIMIT; 
	}

	private boolean isDuplicated(TrackEndorsement endorsement) {
		if (_tracksBeingDownloaded.contains(endorsement.hash)) return true;
		if (my(FileMap.class).getFile(endorsement.hash) != null) return true;
		return false;
	}

	private static boolean isRejected(TrackEndorsement endorsement) {
		return my(RejectedTracksKeeper.class).isRejected(endorsement.hash);
	}

	private boolean hasSpentDownloadAllowance() {
		return peerTracksFolderSize() >= downloadAllowanceInBytes();
	}

	private static long peerTracksFolderSize() {
		return my(IO.class).files().sizeOfFolder(peerTracksFolder());
	}

	private static File peerTracksFolder() {
		return my(TracksFolderKeeper.class).peerTracksFolder();
	}

	private int downloadAllowanceInBytes() {
		return 1024 * 1024 * _downloadAllowance.get().currentValue();
	}

	private boolean isFromTheBestAvailableSource(TrackEndorsement endorsement) {
		Contact bestMatch = my(MusicalTasteMatcher.class).bestMatch().currentValue();
		if (bestMatch == null) return true;
		if (!my(Stethoscope.class).isAlive(bestMatch).currentValue()) return true;

		Contact source = my(ContactSeals.class).contactGiven(endorsement.publisher);
		if(bestMatch.equals(source)) return true;
		return false;
	}

	private static File fileToWrite(TrackEndorsement endorsement) {
		String trackName = new File(endorsement.path).getName();
		Contact peer = my(ContactSeals.class).contactGiven(endorsement.publisher);
		String fileName = (peer == null) ? trackName : peer.nickname().currentValue() + File.separator + trackName;
		return new File(peerTracksFolder(), fileName);
	}

}
