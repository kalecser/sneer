package sneer.bricks.pulp.reactive.collections.impl;

import java.util.List;

import sneer.bricks.pulp.reactive.collections.ListChange;

class CurrentListElements<VO> implements ListChange<VO> {

	private final List<VO> _elements;

	CurrentListElements(List<VO> elements) {
		_elements = elements;
	}

	@Override
	public void accept(ListChange.Visitor<VO> visitor) {
		for (int i = 0; i < _elements.size(); i++)
			visitor.elementAdded(i, _elements.get(i));
	}

}
