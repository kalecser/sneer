package dfcsantos.music.ui.presenter.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import javax.swing.JFileChooser;

import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.skin.filechooser.FileChoosers;
import sneer.bricks.skin.main.instrumentregistry.InstrumentRegistry;
import sneer.foundation.lang.Consumer;
import dfcsantos.music.Music;
import dfcsantos.music.ui.presenter.MusicPresenter;
import dfcsantos.music.ui.view.MusicView;
import dfcsantos.music.ui.view.MusicViewListener;

class MusicPresenterImpl implements MusicPresenter, MusicViewListener {

	@SuppressWarnings("unused") private final Object refToAvoidGc;

	{
    	my(InstrumentRegistry.class).registerInstrument(my(MusicView.class).initInstrument(this));
		checkSharedTracksFolder();
	    refToAvoidGc = my(Music.class).volumePercent().addReceiver(new Consumer<Integer>() { @Override public void consume(Integer volume) {
			my(MusicView.class).setVolume(volume);
		}});
	}

	
	@Override
	public void chooseTracksFolder() {
		my(FileChoosers.class).choose(new Consumer<File>() {  @Override public void consume(File chosenFolder) {
			my(Music.class).setTracksFolder(chosenFolder);
		}}, JFileChooser.DIRECTORIES_ONLY, currentSharedTracksFolder());
	}

	
	@Override
	public Signal<Boolean> isExchangingTracks() {
		return my(Music.class).isTrackExchangeActive();
	}

	
	@Override
	public void toggleTrackExchange() {
		my(Music.class).trackExchangeActivator().consume(!isExchangingTracks().currentValue());
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
	public void shuffle(boolean onOff) {
		my(Music.class).setShuffle(onOff);
	}


	@Override
	public void stop() {
		my(Music.class).stop();
	}
	

	@Override
	public void volume(int percent) {
		my(Music.class).volumePercent(percent);
	}


	private void checkSharedTracksFolder() {
		if (currentSharedTracksFolder() == null)
			chooseTracksFolder();
	}
	
	
	private File currentSharedTracksFolder() {
		return my(Music.class).tracksFolder().currentValue();
	}
	
	
}
