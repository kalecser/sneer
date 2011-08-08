package dfcsantos.music.ui.view.impl;

import sneer.bricks.skin.main.instrumentregistry.Instrument;
import dfcsantos.music.ui.view.MusicView;
import dfcsantos.music.ui.view.MusicViewListener;



class MusicViewImpl implements MusicView {

	private MusicInstrument instrument;
	
	@Override
	public Instrument initInstrument(MusicViewListener listener) {
		if (instrument != null) throw new IllegalStateException();
		instrument = new MusicInstrument(listener);
		return instrument;
	}

	@Override
	public void setVolume(int percent) {
		instrument.setVolume(percent);
	}

}
