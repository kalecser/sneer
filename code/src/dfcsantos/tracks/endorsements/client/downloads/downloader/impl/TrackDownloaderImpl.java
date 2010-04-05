package dfcsantos.tracks.endorsements.client.downloads.downloader.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.Collection;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.hardware.cpu.crypto.Sneer1024;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.ram.collections.CollectionUtils;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReference;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReferences;
import sneer.bricks.hardware.ram.ref.weak.keeper.WeakReferenceKeeper;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.MapRegister;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Functor;
import dfcsantos.tracks.endorsements.client.downloads.counter.TrackDownloadCounter;
import dfcsantos.tracks.endorsements.client.downloads.downloader.TrackDownloader;
import dfcsantos.tracks.endorsements.protocol.TrackEndorsement;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;
import dfcsantos.tracks.storage.rejected.RejectedTracksKeeper;
import dfcsantos.tracks.tastematching.MusicalTasteMatcher;

class TrackDownloaderImpl implements TrackDownloader {

	private static final int CONCURRENT_DOWNLOADS_LIMIT = 3;

	private final ImmutableReference<Signal<Boolean>> _onOffSwitch = my(ImmutableReferences.class).newInstance();

	private final ImmutableReference<Signal<Integer>> _downloadAllowance = my(ImmutableReferences.class).newInstance();

	private final MapRegister<Download, Float> _downloadsAndMatchRatings = my(CollectionSignals.class).newMapRegister();

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


	@Override
	public SetSignal<Download> runningDownloads() {
		return _downloadsAndMatchRatings.output().keys();
	}


	private void consumeTrackEndorsement(final TrackEndorsement endorsement) {
		if (!prepareForDownload(endorsement)) return;

		final Download download = my(FileClient.class).startFileDownload(fileToWrite(endorsement), endorsement.lastModified, endorsement.hash, senderOf(endorsement));
		_downloadsAndMatchRatings.put(download, matchRatingFor(endorsement));

		WeakContract weakContract = download.finished().addPulseReceiver(new Runnable() { @Override public void run() {
			my(TrackDownloadCounter.class).increment(download.hasFinishedSuccessfully());
			_downloadsAndMatchRatings.remove(download);
		}});

		my(WeakReferenceKeeper.class).keep(download, weakContract);
	}


	private boolean prepareForDownload(final TrackEndorsement endorsement) {
		if (!isOn()) return false;

		if (isFromUnknownPublisher(endorsement)) return false;
		if (isFromMe(endorsement)) return false;

		boolean isKnown = isKnown(endorsement);
//		updateMusicalTasteMatcher(endorsement, isKnown);
		if (isKnown) return false;

		if (isRejected(endorsement)) return false;
		if (hasSpentDownloadAllowance()) return false;

//		killDownloadWithTheLowestRatingWorseThan(matchRatingFor(endorsement));
		if (hasReachedDownloadLimit()) return false;

		return true;
	}


	private boolean isOn() {
		if (!_onOffSwitch.isAlreadySet()) return false;
		return _onOffSwitch.get().currentValue();
	}


	private static boolean isFromUnknownPublisher(final TrackEndorsement endorsement) {
		return senderOf(endorsement) == null;
	}


	private static Contact senderOf(TrackEndorsement endorsement) {
		return my(ContactSeals.class).contactGiven(endorsement.publisher);
	}


	private boolean isFromMe(final TrackEndorsement endorsement) {
		return my(OwnSeal.class).get().equals(endorsement.publisher);
	}


	private boolean isKnown(TrackEndorsement endorsement) {
		if (hashesOfRunningDownloads().contains(endorsement.hash)) return true;
		if (my(FileMap.class).getFile(endorsement.hash) != null) return true;
		return false;
	}


	private Collection<Sneer1024> hashesOfRunningDownloads() {
		return my(CollectionUtils.class).map(_downloadsAndMatchRatings.output().keys().currentElements(), new Functor<Download, Sneer1024>() { @Override public Sneer1024 evaluate(Download download) throws RuntimeException {
			return download.hash();
		}});
	}


//	private void updateMusicalTasteMatcher(TrackEndorsement endorsement, boolean isKnownTrack) {
//		Contact sender = senderOf(endorsement);
//		String folder = new File(endorsement.path).getParent();
//		my(MusicalTasteMatcher.class).processEndorsement(sender, folder, isKnownTrack);
//	}


	private static boolean isRejected(TrackEndorsement endorsement) {
		return my(RejectedTracksKeeper.class).isRejected(endorsement.hash);
	}


	private boolean hasSpentDownloadAllowance() {
		return peerTracksFolderSize() >= downloadAllowanceInBytes();
	}


	private static long peerTracksFolderSize() {
		return my(IO.class).files().sizeOfFolder(peerTracksFolder());
	}


	private int downloadAllowanceInBytes() {
		return 1024 * 1024 * _downloadAllowance.get().currentValue();
	}


	private float matchRatingFor(final TrackEndorsement endorsement) {
		Contact sender = senderOf(endorsement);
		String folder = new File(endorsement.path).getParent();
		return my(MusicalTasteMatcher.class).ratingFor(sender, folder);
	}


//	private void killDownloadWithTheLowestRatingWorseThan(float endorsementMatchRating) {
//		Download sentencedToDeath = null;
//		float minMatchRating = endorsementMatchRating;
//
//		for (Entry<Download, Float> downloadAndMatchRating : _downloadsAndMatchRatings.output().currentElements()) {
//			Float matchRating = downloadAndMatchRating.getValue();
//			if (matchRating < minMatchRating) {
//				minMatchRating = matchRating;
//				sentencedToDeath = downloadAndMatchRating.getKey();
//			}
//		}
//
//		if (sentencedToDeath != null) {
//			_downloadsAndMatchRatings.remove(sentencedToDeath);
//			sentencedToDeath.dispose();
//		}
//	}


	private boolean hasReachedDownloadLimit() {
		return _downloadsAndMatchRatings.output().size().currentValue() >= CONCURRENT_DOWNLOADS_LIMIT; 
	}


	private static File fileToWrite(TrackEndorsement endorsement) {
		return new File(peerTracksFolder(), new File(endorsement.path).getName());
	}


	private static File peerTracksFolder() {
		return my(TracksFolderKeeper.class).peerTracksFolder();
	}

}
