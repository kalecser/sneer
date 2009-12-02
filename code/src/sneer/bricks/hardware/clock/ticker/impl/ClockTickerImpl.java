package sneer.bricks.hardware.clock.ticker.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.clock.ticker.ClockTicker;
import sneer.bricks.hardware.clock.ticker.custom.CustomClockTicker;

class ClockTickerImpl implements ClockTicker {

	ClockTickerImpl() {
		my(CustomClockTicker.class).start(10); //Precision of 100 times per second is OK for now.
	}

}
