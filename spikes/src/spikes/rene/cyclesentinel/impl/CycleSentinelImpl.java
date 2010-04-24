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
	
	private Map<Dependency, String> _detailsByDependency = new HashMap<Dependency, String>();
	DirectedGraph<String> _dependencyGraph = my(Graphs.class).createDirectedGraph();

	
	@Override
	public void checkForCycles(String dependentName, String providerName)	throws DependencyCycle {
		new CycleCheck(dependentName, providerName);
	}

	
	private class CycleCheck {

		private String _containerPackage;
		private final String _innermostDependent;   //Ex: "sneer.bricks.hardware.foo"
		private final String _innermostProvider;   //Ex: "sneer.foundation.lang.Functor"
		
		private final Dependency _dependency;

		
		private CycleCheck(String innermostDependent, String innermostProvider) throws DependencyCycle {
			_innermostDependent = innermostDependent;
			_innermostProvider = innermostProvider;

			_dependency = computeOutermostDependency();

			if (_detailsByDependency.containsKey(_dependency))
				return;

			checkForCycle();  // throws DependencyCycle

			_detailsByDependency.put(_dependency, details());
		}

		
		private Dependency computeOutermostDependency() {
			String[] dependentParts = _innermostDependent.split("\\.");
			String[] providerParts = _innermostProvider.split("\\.");

			_containerPackage = "";
			int i = 0;
			while (i < providerParts.length) {
				if (!providerParts[i].equals(dependentParts[i]))
					break;
				_containerPackage += providerParts[i] + ".";
				i++;
			}

			String outermostDependent = _containerPackage + dependentParts[i];
			String outermostProvider = _containerPackage + providerParts[i];
			
			return new Dependency(outermostDependent, outermostProvider);
		}

		
		private String details() {  //Ex: "sneer.bricks -> sneer.foundation  (sneer.bricks.hardware.foo -> sneer.foundation.lang.Functor)"
			String dependent = _dependency.dependent();
			String provider = _dependency.provider();
			
			String result = dependent + " -> " + provider;
			
			if (!dependent.equals(_innermostDependent) || !provider.equals(_innermostProvider))
				result += "  (" + _innermostDependent + " -> " + _innermostProvider + ")";
				
			return result;
		}
		
		
		private void checkForCycle() throws DependencyCycle {
			String dependent = _dependency.dependent();
			String provider = _dependency.provider();
			
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
			return exceptionMessageLine(new Dependency(current, next));
		}


		private String exceptionMessageLine(Dependency currentDependency) {
			String result = currentDependency.equals(_dependency)
				? details()
				: _detailsByDependency.get(currentDependency);
			return "\t" + result;
		}

	}


}
