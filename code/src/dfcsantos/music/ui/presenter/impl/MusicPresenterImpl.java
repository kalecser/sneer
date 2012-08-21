package dfcsantos.music.ui.presenter.impl;

import static basis.environments.Environments.my;

import java.io.File;

import javax.swing.JFileChooser;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.skin.filechooser.FileChoosers;
import sneer.bricks.skin.main.instrumentregistry.InstrumentRegistry;
import basis.lang.Closure;
import basis.lang.Consumer;
import basis.lang.Functor;
import dfcsantos.music.Music;
import dfcsantos.music.ui.presenter.MusicPresenter;
import dfcsantos.music.ui.view.MusicView;
import dfcsantos.music.ui.view.MusicViewListener;
import dfcsantos.tracks.Track;

class MusicPresenterImpl implements MusicPresenter, MusicViewListener {

	private static final int FIVE_MINUTES = 1000 * 60 * 5;
	private static final String INBOX = "<Inbox>";
	private static final String ALL_TRACKS = "<All Tracks>";
	
	private final ListRegister<String> _playingFolderChoices = my(CollectionSignals.class).newListRegister();
	private String playingFolder;
	
	@SuppressWarnings("unused")	private WeakContract refToAvoidGc1, refToAvoidGc2, refToAvoidGc3, refToAvoidGc4;
	
	private Register<String> _choiceSelected = my(Signals.class).newRegister(null); 
	private Register<Boolean> _meTooEnable = my(Signals.class).newRegister(true);
	private Register<Boolean> _trackDownloadedEnable = my(Signals.class).newRegister(false);

	
	{
		my(MusicView.class).setListener(this);
    	my(InstrumentRegistry.class).registerInstrument(my(MusicView.class));
    	
		if (currentSharedTracksFolder() == null)
			chooseTracksFolder();

		initChoicesRefresh();

		refToAvoidGc4 = my(Music.class).numberOfPeerTracks().addReceiver(new Consumer<Integer>() { @Override public void consume(Integer numberPeerTracks) {
			newTrackDownloaded(numberPeerTracks);
		}}); 
	}


	@Override
	public void chooseTracksFolder() {
		my(FileChoosers.class).choose(JFileChooser.DIRECTORIES_ONLY, currentSharedTracksFolder(), new Consumer<File>() {  @Override public void consume(File chosenFolder) {
			my(Music.class).setTracksFolder(chosenFolder);
		}});
	}


	@Override
	public Register<Boolean> isTrackExchangeActive() {
		return my(Music.class).isTrackExchangeActive();
	}

	
	@Override
	public Signal<Boolean> isPlaying() {
		return my(Music.class).isPlaying();
	}
	
	@Override
	public Signal<String> playingTrackName() {
		return my(Signals.class).adapt(my(Music.class).playingTrack(), new Functor<Track, String>() { @Override public String evaluate(Track track) {
			return (track == null) ? "<No track to play>" : track.name();
		}});
	}

	
	@Override
	public Signal<Integer> playingTrackTime() {
		return my(Music.class).playingTrackTime();
	}

	
	private File currentSharedTracksFolder() {
		return my(Music.class).tracksFolder().currentValue();
	}

	
	@Override
	public void pauseResume() {
		my(Music.class).pauseResume();
	}

	
	@Override
	public void skip() {
		my(Music.class).skip();
	}

	
	@Override
	public void stop() {
		my(Music.class).stop();
	}
	
	
	@Override
	public void meToo() {
		my(Music.class).meToo();
	}

	
	@Override
	public void meh() {
		my(Music.class).meh();
	}


	@Override
	public void noWay() {
		my(Music.class).noWay();
	}


	@Override
	public Register<Integer> volumePercent() {
		return my(Music.class).volumePercent();
	}


	@Override
	public Register<Boolean> shuffle() {
		return my(Music.class).shuffle();
	}

	
	@Override
	public void playingInboxFolder() {
		String inbox = inboxChoice(); 
		playingFolderChosen(inbox);
		_choiceSelected.setter().consume(inbox);
	}

	
	@Override
	public void playingFolderChosen(String folderChosen) {
		playingFolder = folderChosen;
		if (folderChosen.startsWith(INBOX))
			changeToPeersModeAndPlayTracks();
		else 
			changeToOwnModeAndPlayTracksOf(folderChosen);
	}

	
	@Override
	public ListSignal<String> playingFolderChoices() {
		return _playingFolderChoices.output();
	}

	
	@Override
	public Signal<Boolean> enableMeToo() {
		return _meTooEnable.output();
	}


	@Override
	public Signal<Boolean> enableTrackDownloaded() {
		return _trackDownloadedEnable.output();
	}

	
	@Override
	public Signal<String> choiceSelected() {
		return _choiceSelected.output();
	}

	private void newTrackDownloaded(Integer numberTracksDownloaded) {
		if (numberTracksDownloaded > 0) 
			_trackDownloadedEnable.setter().consume(true);
		else
			_trackDownloadedEnable.setter().consume(false);
	}
	
	
	private void changeToPeersModeAndPlayTracks() {
		my(Music.class).setOperatingMode(Music.OperatingMode.PEERS);
		my(Music.class).setPlayingFolder(my(Music.class).playingFolder());

		_meTooEnable.setter().consume(true);
		_trackDownloadedEnable.setter().consume(false);
	}
	
	private void changeToOwnModeAndPlayTracksOf(String folderChosen) {
		my(Music.class).setOperatingMode(Music.OperatingMode.OWN);
		File folderChosenToPlay = whatFolderChosen(folderChosen); //
		my(Music.class).setPlayingFolder(folderChosenToPlay);
		_meTooEnable.setter().consume(false);
	}
	
	private File whatFolderChosen(String folderChosen) {
		return folderChosen.equals(ALL_TRACKS) ? currentSharedTracksFolder() : new File(currentSharedTracksFolder(), folderChosen);
	}
	

	private void initChoicesRefresh() {
		final Closure choicesRefresh = new Closure() { @Override public void run() {
			refreshChoices();
		}};
		
		my(Threads.class).startDaemon("MusicPresenter init.", new Closure() { @Override public void run() {
			refToAvoidGc1 = my(Timer.class).wakeUpNowAndEvery(FIVE_MINUTES, choicesRefresh);
			refToAvoidGc2 = my(Music.class).tracksFolder().addPulseReceiver(choicesRefresh);
			refToAvoidGc3 = my(Music.class).numberOfPeerTracks().addPulseReceiver(choicesRefresh);
		}});
	}

	
	synchronized
	private void refreshChoices() {
		clearChoices();
		addChoice(inboxChoice());
		addChoice(ALL_TRACKS);
		addSubFoldersIfNecessary();
		my(Logger.class).log("Choices refreshed: ", INBOX, " ", ALL_TRACKS, " sub folders.");
	}


	private String inboxChoice() {
		return INBOX + " " + my(Music.class).numberOfPeerTracks().currentValue() + " Tracks";
	}


	private void clearChoices() {
		while (_playingFolderChoices.output().size().currentValue() > 0)
			_playingFolderChoices.removeAt(0);
	}

	
	private void addSubFoldersIfNecessary() {
		File root = currentSharedTracksFolder();
		if (root == null) return;
		for (String choice : new FolderChoicesPoll(root.toPath()).result())
			addChoice(choice);
	}

	private void addChoice(String choice) {
		_playingFolderChoices.add(choice);
	}


	@Override
	public String playingFolder() {
		if (playingFolder == null) return null;
		if (playingFolder.startsWith(INBOX)) return inboxChoice();
		return playingFolder;
	}

}