package sneer.bricks.hardware.ram.graphs;

import basis.brickness.Brick;

@Brick
public interface Graphs {

	<T> DirectedGraph<T> createDirectedGraph();

}
