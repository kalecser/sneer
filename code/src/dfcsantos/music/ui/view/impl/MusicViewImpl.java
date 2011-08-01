package dfcsantos.music.ui.view.impl;

import sneer.bricks.skin.main.instrumentregistry.Instrument;
import dfcsantos.music.ui.view.MusicView;
import dfcsantos.music.ui.view.MusicViewListener;



class MusicViewImpl implements MusicView {

	private boolean alreadyInitialized;
	
	@Override
	public Instrument initInstrument(MusicViewListener listener) {
		if (alreadyInitialized) throw new IllegalStateException();
		alreadyInitialized = true;
		return new MusicInstrument(listener);
	}

}
