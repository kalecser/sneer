package spikes.klaus.wanderer.sneer;

import java.util.List;
import java.util.Random;

public class Chooser extends Random {
	public Chooser() {
		super(0); //Seed for pseudo-randomness.
	}

	public <T> T pickOne(List<T> options) {
		return options.get(nextInt(options.size()));
	}

	public <T> T pickOne(T[] options) {
		return options[nextInt(options.length)];
	}

	public <T> T pickOneExcept(T[] options, T excluded) {
		while (true) {
			T chosen = pickOne(options);
			if (!chosen.equals(excluded))
				return chosen;
			if (options.length < 2) throw new IllegalArgumentException("There was only " + options.length + " option to pick.");
		}
	}
}
