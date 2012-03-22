package dfcsantos.tracks.exchange.downloads.downloader.impl;

import static basis.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map.Entry;

import basis.lang.Consumer;
import basis.lang.Functor;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.files.atomic.dotpart.DotParts;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.ram.collections.CollectionUtils;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReference;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReferences;
import sneer.bricks.hardware.ram.ref.weak.keeper.WeakReferenceKeeper;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.MapRegister;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import dfcsantos.tracks.exchange.downloads.counter.TrackDownloadCounter;
import dfcsantos.tracks.exchange.downloads.downloader.TrackDownloader;
import dfcsantos.tracks.exchange.endorsements.TrackEndorsement;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;
import dfcsantos.tracks.storage.rejected.RejectedTracksKeeper;
import dfcsantos.tracks.tastematching.TasteMatcher;

class TrackDownloaderImpl implements TrackDownloader {

	private static final int CONCURRENT_DOWNLOADS_LIMIT = 3;
	private static final int TRACK_DOWNLOADED_LIMIT = 10;

	private final ImmutableReference<Signal<Boolean>> _onOffSwitch = my(ImmutableReferences.class).newInstance();

	private final MapRegister<Download, Float> _downloadsAndMatchRatings = my(CollectionSignals.class).newMapRegister();

	@SuppressWarnings("unused") private final WeakContract _toAvoidGC;

	{
		clearOldDotPartFiles();
		
		_toAvoidGC = my(RemoteTuples.class).addSubscription(TrackEndorsement.class, new Consumer<TrackEndorsement>() { @Override public void consume(TrackEndorsement endorsement) {
			consumeTrackEndorsement(endorsement);
		}});
	}


	private void clearOldDotPartFiles() {
		try {
			my(DotParts.class).deleteAllDotPartsRecursively(inbox());
		} catch (IOException e) {
			my(BlinkingLights.class).turnOn(LightType.WARNING, "Unable to clean old downloads from Music inbox", "This will waste space.", e, 20000);
		}
	}

	
	@Override
	public void setOnOffSwitch(Signal<Boolean> onOffSwitch) {
		_onOffSwitch.set(onOffSwitch);
	}

	
	@Override
	public SetSignal<Download> runningDownloads() {
		return _downloadsAndMatchRatings.output().keys();
	}

	
	private void consumeTrackEndorsement(final TrackEndorsement endorsement) {
		Float rating = prepareForDownload(endorsement);
		if (rating == null) return;

		final Download download = my(FileClient.class).startFileDownload(fileToWrite(endorsement), endorsement.lastModified, endorsement.hash, endorsement.publisher);
		_downloadsAndMatchRatings.put(download, rating);

		WeakContract weakContract = download.finished().addReceiver(new Consumer<Boolean>() { @Override public void consume(Boolean finished) {
			if (finished)
				dealWithFinishedDownload(download);
		}});

		my(WeakReferenceKeeper.class).keep(download, weakContract);
	}


	private Float prepareForDownload(final TrackEndorsement endorsement) {
		if (!isOn()) { log("TrackDownloader Off"); return null; }

		if (isFromUnknownPublisher(endorsement)) { log("Unkown Publisher"); return null; }

		boolean isKnown = my(RejectedTracksKeeper.class)
			.isWeakRejected(endorsement.hash) ? null : isKnown(endorsement);
		float rating = rate(endorsement, opinionOn(isKnown));
		if (isKnown) { log("Duplicated Track"); return null; }

		if (isRejected(endorsement)) { log("Rejected Track"); return null; }
		if (isInboxFull()) { log("Inbox Full"); return null; }

		killDownloadWithTheLowestRatingWorseThan(rating);
		if (hasReachedDownloadLimit()) { log("Concurrent Download Limit Reached"); return null; }

		return rating;
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


	private float rate(TrackEndorsement e, Boolean opinion) {
		return my(TasteMatcher.class).rateEndorsement(senderOf(e), folderFor(e), opinion);
	}


	private Boolean opinionOn(boolean isKnownTrack) {
		return isKnownTrack ? true : null;
	}


	private String folderFor(TrackEndorsement endorsement) {
		String result = new File(endorsement.path).getParent();
		return (result == null) ? "" : result;
	}


	private static boolean isRejected(TrackEndorsement endorsement) {
		return my(RejectedTracksKeeper.class).isRejected(endorsement.hash);
	}


	private boolean isInboxFull() {
		return my(TrackDownloadCounter.class).count().currentValue() >= TRACK_DOWNLOADED_LIMIT;
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


	private void dealWithFinishedDownload(final Download download) {
		my(TrackDownloadCounter.class).refresh();
		_downloadsAndMatchRatings.remove(download);
	}


	private static File fileToWrite(TrackEndorsement endorsement) {
		return new File(inbox(), new File(endorsement.path).getName());
	}


	private static File inbox() {
		return my(TracksFolderKeeper.class).inboxFolder();
	}

}
