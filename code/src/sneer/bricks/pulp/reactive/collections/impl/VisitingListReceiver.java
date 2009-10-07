package sneer.bricks.pulp.reactive.collections.impl;

import sneer.bricks.pulp.reactive.collections.ListChange;
import sneer.bricks.pulp.reactive.collections.ListChange.Visitor;
import sneer.foundation.lang.Consumer;

class VisitingListReceiver<T> implements Consumer<ListChange<T>> {

	private final Visitor<T> _delegate;

	public VisitingListReceiver(Visitor<T> delegate) {
		_delegate = delegate;
	}

	@Override
	public void consume(ListChange<T> listChange) {
		listChange.accept(_delegate);
	}
}
