package sneer.bricks.skin.main.instrumentregistry;

import basis.brickness.Brick;
import sneer.bricks.pulp.reactive.collections.ListSignal;

@Brick
public interface InstrumentRegistry {

	void registerInstrument(Instrument instrument);

	ListSignal<Instrument> installedInstruments();
}
