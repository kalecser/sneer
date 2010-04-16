package spikes.rene.cyclesentinel;

public interface CycleSentinel {

	void checkForCycles(String dependentClass, String providerClass) throws DependencyCycle;
	
}
