package dfcsantos.music.ui.presenter.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.skin.main.instrumentregistry.InstrumentRegistry;
import dfcsantos.music.ui.presenter.MusicPresenter;
import dfcsantos.music.ui.view.MusicView;

class MusicPresenterImpl implements MusicPresenter {

	{
		my(InstrumentRegistry.class).registerInstrument(my(MusicView.class));
	}
	
}
