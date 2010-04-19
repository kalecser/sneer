package spikes.rene.cyclesentinel.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sneer.bricks.hardware.ram.graphs.DirectedGraph;
import sneer.bricks.hardware.ram.graphs.Graphs;
import spikes.rene.cyclesentinel.CycleSentinel;
import spikes.rene.cyclesentinel.DependencyCycle;


public class CycleSentinelImpl implements CycleSentinel {
	
	private static final String[] STRING_ARRAY = new String[] {};
	
	private Map<Dependency, String> _debugInfosByDependency = new HashMap<Dependency, String>();
	DirectedGraph<String> _dependencyGraph = my(Graphs.class).createDirectedGraph();

	
	@Override
	public void checkForCycles(String dependentName, String providerName)	throws DependencyCycle {
		String[] dependentParts = dependentName.split("\\.");
		String[] providerParts = providerName.split("\\.");

		String commonRoot = "";
		int i = 0;
		while (i < providerParts.length) {
			if (!providerParts[i].equals(dependentParts[i]))
				break;
			commonRoot += providerParts[i] + ".";
			i++;
		}

		String providerRoot = commonRoot + providerParts[i];
		String dependentRoot = commonRoot + dependentParts[i];

		Dependency dependency = new Dependency(dependentRoot, providerRoot);
		if (_debugInfosByDependency.containsKey(dependency))
			return;
		String debugInfo = dependentName + " -> " + providerName;
		_debugInfosByDependency.put(dependency, debugInfo);

		try {
			checkForRootCycles(dependentRoot, providerRoot);
		} catch (DependencyCycle e) {
			_debugInfosByDependency.remove(dependency);
			throw e;
		}
	}

	private void checkForRootCycles(String dependent, String provider) throws DependencyCycle {
		_dependencyGraph.addEdge(dependent, provider);
		List<String> cycle = _dependencyGraph.detectCycle();
		if (cycle.isEmpty())
			return;
		
		_dependencyGraph.removeEdge(dependent, provider);
		
		throw new DependencyCycle(exceptionMessageFor(cycle.toArray(STRING_ARRAY)));
	}

	
	private String exceptionMessageFor(String[] cycle) {
		String result = "Dependency cycle detected:\n";
		
		for (int i = 0; i < cycle.length - 1; i++) {
			String current = cycle[i];
			String next = cycle[i + 1];
			result += exceptionMessageLine(current, next) + "\n";
		}
		return result + exceptionMessageLine(cycle[cycle.length - 1], cycle[0]);
	}

	private String exceptionMessageLine(String current, String next) {
		return "\t" + _debugInfosByDependency.get(new Dependency(current, next));
	}

}
