package dfcsantos.music.ui.presenter.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import javax.swing.JFileChooser;

import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.skin.filechooser.FileChoosers;
import sneer.bricks.skin.main.instrumentregistry.InstrumentRegistry;
import sneer.foundation.lang.Consumer;
import dfcsantos.music.Music;
import dfcsantos.music.ui.presenter.MusicPresenter;
import dfcsantos.music.ui.view.MusicView;
import dfcsantos.music.ui.view.MusicViewListener;

class MusicPresenterImpl implements MusicPresenter, MusicViewListener {

	{
		my(MusicView.class).setListener(this);
    	my(InstrumentRegistry.class).registerInstrument(my(MusicView.class));
		checkSharedTracksFolder();
	}

	
	@Override
	public void chooseTracksFolder() {
		my(FileChoosers.class).choose(new Consumer<File>() {  @Override public void consume(File chosenFolder) {
			my(Music.class).setTracksFolder(chosenFolder);
		}}, JFileChooser.DIRECTORIES_ONLY, currentSharedTracksFolder());
	}

	
	@Override
	public Register<Boolean> isTrackExchangeActive() {
		return my(Music.class).isTrackExchangeActive();
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


	private void checkSharedTracksFolder() {
		if (currentSharedTracksFolder() == null)
			chooseTracksFolder();
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
	
	
}
