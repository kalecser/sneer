package spikes.adenauer.holepunching.prevalence;

import java.util.HashMap;
import java.util.Map;

import spikes.adenauer.holepunching.Node;


public class AllNodes {
	private static final Map<String, Node> nodesById = new HashMap<String, Node>();

	public static void add(String key, Node node) {
		nodesById.put(key, node);
	}
}
