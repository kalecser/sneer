package dfcsantos.music.ui.presenter.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFileChooser;

import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
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

	private Register<Set<String>> _subSharedTrackFolder = my(Signals.class).newRegister(null);

	
	{
		my(MusicView.class).setListener(this);
    	my(InstrumentRegistry.class).registerInstrument(my(MusicView.class));
		checkSharedTracksFolder();
	}

	
	@Override
	public void chooseTracksFolder() {
		my(FileChoosers.class).choose(new Consumer<File>() {  @Override public void consume(File chosenFolder) {
			my(Music.class).setTracksFolder(chosenFolder);
			loadSubSharedTracksFolder(chosenFolder);
		}}, JFileChooser.DIRECTORIES_ONLY, currentSharedTracksFolder());
	}


	@Override
	public Register<Boolean> isTrackExchangeActive() {
		return my(Music.class).isTrackExchangeActive();
	}


	@Override
	public Signal<Set<String>> subSharedTracksFolders() {
		return _subSharedTrackFolder.output();
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
	public void deleteTrack() {
		my(Music.class).deleteTrack();
	}

	
	@Override
	public void meToo() {
		my(Music.class).meToo(); //Reimplement this method to increase taste musical.
	}


	@Override
	public void noWay() {
		deleteTrack(); //Reimplement this method to decrease taste musical. 
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

	
	private void checkSharedTracksFolder() {
		if (currentSharedTracksFolder() == null)
			chooseTracksFolder();
		else
			loadSubSharedTracksFolder(currentSharedTracksFolder());
	}
	
	
	private File currentSharedTracksFolder() {
		return my(Music.class).tracksFolder().currentValue();
	}


	@Override
	public Register<Integer> volumePercent() {
		return my(Music.class).volumePercent();
	}


	@Override
	public Register<Boolean> shuffle() {
		return my(Music.class).shuffle();
	}
	
	
	private void loadSubSharedTracksFolder(File sharedTracksFolder) {
		Set<String> subFordersPaths = new HashSet<String>();  
		loadSubFolders(sharedTracksFolder.getAbsolutePath(), sharedTracksFolder, subFordersPaths);
		_subSharedTrackFolder.setter().consume(subFordersPaths);
	}

	private void loadSubFolders(final String sharedTracksFolderPath, File folder, Set<String> subFordersPaths) {
		if (folder == null) return;
		if (folder.isFile()) return;
		
		loadSubFolderPath(sharedTracksFolderPath, folder.getAbsolutePath(), subFordersPaths);
			
		for (File subFolder : folder.listFiles())
			loadSubFolders(sharedTracksFolderPath, subFolder, subFordersPaths);
	}

	private void loadSubFolderPath(String sharedTracksFolderPath, String subFolderPath, Set<String> subFordersPaths) {
		 if (subFolderPath == null) return;
		 if (subFolderPath.equals(sharedTracksFolderPath)) return;
		 String result = subFolderPath.replace(sharedTracksFolderPath + File.separator, "");
		 subFordersPaths.add(result);
	}
}
