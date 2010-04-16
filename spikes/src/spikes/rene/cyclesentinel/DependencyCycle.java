package spikes.rene.cyclesentinel;


public class DependencyCycle extends Exception {

	public DependencyCycle(String message) {
		super(message);
	}

}
