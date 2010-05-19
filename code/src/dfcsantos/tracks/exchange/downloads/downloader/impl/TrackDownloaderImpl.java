package dfcsantos.tracks.exchange.downloads.downloader.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.Collection;
import java.util.Map.Entry;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.ram.collections.CollectionUtils;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReference;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReferences;
import sneer.bricks.hardware.ram.ref.weak.keeper.WeakReferenceKeeper;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.MapRegister;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Functor;
import dfcsantos.tracks.exchange.downloads.counter.TrackDownloadCounter;
import dfcsantos.tracks.exchange.downloads.downloader.TrackDownloader;
import dfcsantos.tracks.exchange.endorsements.TrackEndorsement;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;
import dfcsantos.tracks.storage.rejected.RejectedTracksKeeper;
import dfcsantos.tracks.tastematching.MusicalTasteMatcher;

class TrackDownloaderImpl implements TrackDownloader {

	private static final int CONCURRENT_DOWNLOADS_LIMIT = 3;

	private final ImmutableReference<Signal<Boolean>> _onOffSwitch = my(ImmutableReferences.class).newInstance();

	private final ImmutableReference<Signal<Integer>> _downloadAllowance = my(ImmutableReferences.class).newInstance();

	private final MapRegister<Download, Float> _downloadsAndMatchRatings = my(CollectionSignals.class).newMapRegister();

	@SuppressWarnings("unused") private final WeakContract _toAvoidGC;

	{
		_toAvoidGC = my(RemoteTuples.class).addSubscription(TrackEndorsement.class, new Consumer<TrackEndorsement>() { @Override public void consume(TrackEndorsement endorsement) {
			consumeTrackEndorsement(endorsement);
		}});
	}

	
	@Override
	public void setOnOffSwitch(Signal<Boolean> onOffSwitch) {
		_onOffSwitch.set(onOffSwitch);
	}


	@Override
	public void setDownloadAllowance(Signal<Integer> downloadAllowance) {
		_downloadAllowance.set(downloadAllowance);
	}


	@Override
	public SetSignal<Download> runningDownloads() {
		return _downloadsAndMatchRatings.output().keys();
	}


	private void consumeTrackEndorsement(final TrackEndorsement endorsement) {
		if (!prepareForDownload(endorsement)) return;

		final Download download = my(FileClient.class).startFileDownload(fileToWrite(endorsement), endorsement.lastModified, endorsement.hash, endorsement.publisher);
		_downloadsAndMatchRatings.put(download, matchRatingFor(endorsement));

		WeakContract weakContract = download.finished().addPulseReceiver(new Runnable() { @Override public void run() {
			my(TrackDownloadCounter.class).increment(download.hasFinishedSuccessfully());
			_downloadsAndMatchRatings.remove(download);
		}});

		my(WeakReferenceKeeper.class).keep(download, weakContract);
	}


	private boolean prepareForDownload(final TrackEndorsement endorsement) {
		if (!isOn()) { log("TrackDownloader Off"); return false; }

		if (isFromUnknownPublisher(endorsement)) { log("Unkown Publisher"); return false; }

		boolean isKnown = isKnown(endorsement);
		updateMusicalTasteMatcher(endorsement, isKnown);
		if (isKnown) { log("Duplicated Track"); return false; }

		if (isRejected(endorsement)) { log("Rejected Track"); return false; }
		if (hasSpentDownloadAllowance()) { log("Download Space Allowance Reached"); return false; }

		killDownloadWithTheLowestRatingWorseThan(matchRatingFor(endorsement));
		if (hasReachedDownloadLimit()) { log("Concurrent Download Limit Reached"); return false; }

		return true;
	}


	private void log(String cause) {
		my(Logger.class).log("TrackEndorsement Refusal: ", cause);
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


	private boolean isKnown(TrackEndorsement endorsement) {
		if (hashesOfRunningDownloads().contains(endorsement.hash)) return true;
		if (my(FileMap.class).getFile(endorsement.hash) != null) return true;
		return false;
	}


	private Collection<Hash> hashesOfRunningDownloads() {
		return my(CollectionUtils.class).map(_downloadsAndMatchRatings.output().keys().currentElements(), new Functor<Download, Hash>() { @Override public Hash evaluate(Download download) throws RuntimeException {
			return download.hash();
		}});
	}


	private void updateMusicalTasteMatcher(TrackEndorsement endorsement, boolean isKnownTrack) {
		String nickname = senderOf(endorsement).nickname().currentValue();
		String folder = new File(endorsement.path).getParent();
		my(MusicalTasteMatcher.class).processEndorsement(nickname, (folder == null) ? "" : folder, isKnownTrack);
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


	private long downloadAllowanceInBytes() {
		return 1024l * 1024l * _downloadAllowance.get().currentValue();
	}


	private float matchRatingFor(final TrackEndorsement endorsement) {
		String nickname = senderOf(endorsement).nickname().currentValue();
		String folder = new File(endorsement.path).getParent();
		return my(MusicalTasteMatcher.class).ratingFor(nickname, (folder == null) ? "" : folder);
	}


	private void killDownloadWithTheLowestRatingWorseThan(float endorsementMatchRating) {
		Download sentencedToDeath = null;
		float minMatchRating = endorsementMatchRating;

		for (Entry<Download, Float> downloadAndMatchRating : _downloadsAndMatchRatings.output().currentElements()) {
			Float matchRating = downloadAndMatchRating.getValue();
			if (matchRating < minMatchRating) {
				minMatchRating = matchRating;
				sentencedToDeath = downloadAndMatchRating.getKey();
			}
		}

		if (sentencedToDeath != null) {
			my(Logger.class).log("Killing download with the lowest rating: {} ({})", sentencedToDeath.file(), minMatchRating);
			_downloadsAndMatchRatings.remove(sentencedToDeath);
			sentencedToDeath.dispose();
		}
	}


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
