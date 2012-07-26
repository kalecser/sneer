package sneer.bricks.skin.widgets.reactive;

import javax.swing.JList;

import sneer.bricks.pulp.reactive.Signal;

public interface ListWidget<E> extends ComponentWidget<JList<E>>{

	Signal<E> selectedElement();
	void clearSelection();

}
