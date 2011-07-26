package dfcsantos.music.ui.presenter.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.main.instrumentregistry.InstrumentRegistry;
import dfcsantos.music.ui.presenter.MusicPresenter;
import dfcsantos.music.ui.view.MusicView;
import dfcsantos.music.ui.view.MusicViewListener;

class MusicPresenterImpl implements MusicPresenter, MusicViewListener {

	private Register<Boolean> isExchangingTracks = my(Signals.class).newRegister(false);

	{
		my(InstrumentRegistry.class).registerInstrument(my(MusicView.class).initInstrument(this));
	}

	@Override
	public void chooseTracksFolder() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public Signal<Boolean> isExchangingTracks() {
		return isExchangingTracks.output();
	}

	@Override
	public void toggleTrackExchange() {
		isExchangingTracks.setter().consume(!isExchangingTracks().currentValue());
	}
	
}
