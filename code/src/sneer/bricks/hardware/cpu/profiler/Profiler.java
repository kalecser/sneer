package sneer.bricks.hardware.cpu.profiler;

import java.io.PrintStream;

import sneer.bricks.pulp.reactive.collections.MapSignal;
import sneer.bricks.software.bricks.snapploader.Snapp;
import sneer.foundation.brickness.Brick;

@Snapp
@Brick
public interface Profiler {

	MapSignal<String, Float> percentagesByMethod();

	void dumpTo(PrintStream out);

}
