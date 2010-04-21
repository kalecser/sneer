package spikes.rene.cyclesentinel;

public interface CycleSentinel {

	void checkForCycles(String dependentName, String providerName) throws DependencyCycle;
	
}
