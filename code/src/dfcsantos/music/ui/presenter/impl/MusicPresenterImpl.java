package dfcsantos.music.ui.presenter.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.skin.filechooser.FileChoosers;
import sneer.bricks.skin.main.instrumentregistry.InstrumentRegistry;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Functor;
import dfcsantos.music.Music;
import dfcsantos.music.ui.presenter.MusicPresenter;
import dfcsantos.music.ui.view.MusicView;
import dfcsantos.music.ui.view.MusicViewListener;
import dfcsantos.tracks.Track;

class MusicPresenterImpl implements MusicPresenter, MusicViewListener {

	private static final int FIVE_MINUTES = 1000 * 60 * 5;
	private static final String INBOX = "<Inbox>";
	
	private final ListRegister<String> _playingFolderChoices = my(CollectionSignals.class).newListRegister();
	private FolderChoicesPoll _folderChoicesPoll;
	
	@SuppressWarnings("unused")	private WeakContract refToAvoidGc1, refToAvoidGc2, refToAvoidGc3;

	
	{
		my(MusicView.class).setListener(this);
    	my(InstrumentRegistry.class).registerInstrument(my(MusicView.class));
    	
    	initChoicesRefresh();
    	
		if (currentSharedTracksFolder() == null)
			chooseTracksFolder();
	}


	@Override
	public void chooseTracksFolder() {
		my(FileChoosers.class).choose(new Consumer<File>() {  @Override public void consume(File chosenFolder) {
			my(Music.class).setTracksFolder(chosenFolder);
			_folderChoicesPoll = new FolderChoicesPoll(chosenFolder);
		}}, JFileChooser.DIRECTORIES_ONLY, currentSharedTracksFolder());
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
	public void playingFolderChosen(String subSharedFolder) {
		if (subSharedFolder.startsWith(INBOX)) 
			changeToPeersModeAndPlayTracks();
		else 
			changeToOwnModeAndPlayTracksOf(subSharedFolder);
	}

	
	@Override
	public ListSignal<String> playingFolderChoices() {
		return _playingFolderChoices.output();
	}

	
	private void changeToPeersModeAndPlayTracks() {
		my(Music.class).setOperatingMode(Music.OperatingMode.PEERS);
		my(Music.class).setPlayingFolder(my(Music.class).playingFolder());
	}
	
	private void changeToOwnModeAndPlayTracksOf(String subSharedFolder) {
		my(Music.class).setOperatingMode(Music.OperatingMode.OWN);
		File folderChosenToPlay =  new File(currentSharedTracksFolder(), subSharedFolder);
		my(Music.class).setPlayingFolder(folderChosenToPlay);
	}
	

	private void initChoicesRefresh() {
		_folderChoicesPoll = new FolderChoicesPoll(currentSharedTracksFolder());
		
		Runnable choicesRefresh = new Runnable(){ @Override public void run() {
			refreshChoices();
		}};
		
		refToAvoidGc1 = my(Timer.class).wakeUpNowAndEvery(FIVE_MINUTES, choicesRefresh);
		refToAvoidGc2 = my(Music.class).tracksFolder().addPulseReceiver(choicesRefresh);
		refToAvoidGc3 = my(Music.class).numberOfPeerTracks().addPulseReceiver(choicesRefresh);
	}

	
	private void refreshChoices() {
		addChoice(INBOX + " " + my(Music.class).numberOfPeerTracks().currentValue() + " Tracks");
		addSubFolders();
		my(Logger.class).log("Choices refreshed: ", "<inbox> ", "sub folders.");
	}


	private void addSubFolders() {
		List<String> allChoices = _folderChoicesPoll.result();
		if (allChoices == null) return;
		for (String choice : allChoices)
			addChoice(choice);
	}


	private void addChoice(String choice) {
		if (choice == null) return;
		if (_playingFolderChoices.output().currentElements().contains(choice)) return; //Avoid duplication
		_playingFolderChoices.add(choice);
	}

}