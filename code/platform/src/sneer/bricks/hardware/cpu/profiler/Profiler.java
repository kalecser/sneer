package sneer.bricks.hardware.cpu.profiler;

import sneer.bricks.pulp.reactive.collections.MapSignal;
import sneer.bricks.software.bricks.snappstarter.Snapp;
import sneer.foundation.brickness.Brick;

@Snapp
@Brick
public interface Profiler {

	MapSignal<String, Float> percentagesByMethod();

}
