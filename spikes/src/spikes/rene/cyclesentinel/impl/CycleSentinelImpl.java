package spikes.rene.cyclesentinel.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import spikes.rene.cyclesentinel.CycleSentinel;
import spikes.rene.cyclesentinel.DependencyCycle;

public class CycleSentinelImpl implements CycleSentinel {
	
	Map<String, Set<String>> _providersByClass = new HashMap<String, Set<String>>();
	
	@Override
	public void checkForCycles(String dependentClass, String providerClass)	throws DependencyCycle {
		if (providersFor(providerClass).contains(dependentClass))
			throw new DependencyCycle("" + providerClass + " already depends on " + dependentClass);

		providersFor(dependentClass).add(providerClass);
	}

	private Set<String> providersFor(String className) {
		Set<String> result = _providersByClass.get(className);
		if (result == null) {
			result = new HashSet<String>();
			_providersByClass.put(className, result);
		}
		return result;
	}

}
