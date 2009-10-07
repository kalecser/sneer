package sneer.bricks.hardware.cpu.profiler;

import sneer.bricks.pulp.reactive.collections.MapSignal;
import sneer.foundation.brickness.Brick;

//@Snapp
@Brick
public interface Profiler {

	MapSignal<String, Float> percentagesByMethod();

}
