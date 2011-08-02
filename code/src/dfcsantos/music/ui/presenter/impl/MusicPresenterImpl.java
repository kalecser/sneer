package dfcsantos.music.ui.presenter.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import javax.swing.JFileChooser;

import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.skin.main.instrumentregistry.InstrumentRegistry;
import sneer.bricks.skin.notmodal.filechooser.FileChoosers;
import sneer.foundation.lang.Consumer;
import dfcsantos.music.Music;
import dfcsantos.music.ui.presenter.MusicPresenter;
import dfcsantos.music.ui.view.MusicView;
import dfcsantos.music.ui.view.MusicViewListener;

class MusicPresenterImpl implements MusicPresenter, MusicViewListener {

	private final JFileChooser tracksFolderChooser;

	{
        tracksFolderChooser = initTracksFolderChooser();
    	my(InstrumentRegistry.class).registerInstrument(my(MusicView.class).initInstrument(this));
	}

	
	private JFileChooser initTracksFolderChooser() {
		JFileChooser chooser = my(FileChoosers.class).newFileChooser(new Consumer<File>() { @Override public void consume(File chosenFolder) {
        	if (chosenFolder != null)
        		my(Music.class).setSharedTracksFolder(chosenFolder);
    	}}, JFileChooser.DIRECTORIES_ONLY);
		chooser.setCurrentDirectory(my(Music.class).sharedTracksFolder().currentValue());
		return chooser;
	}

	@Override
	public void chooseTracksFolder() {
    	tracksFolderChooser.showOpenDialog(null);
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
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public void skip() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public void shuffle() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public void stop() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}
}
