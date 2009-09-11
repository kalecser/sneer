package sneer.bricks.skin.widgets.reactive.impl;

import static sneer.foundation.environments.Environments.my;

import javax.swing.AbstractListModel;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.collections.ListChange;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.pulp.reactive.signalchooser.ListOfSignalsReceiver;
import sneer.bricks.pulp.reactive.signalchooser.SignalChooser;
import sneer.bricks.pulp.reactive.signalchooser.SignalChoosers;

class ListSignalModel<T> extends AbstractListModel {

	private static final SignalChoosers _SignalChoosers = my(SignalChoosers.class);

	
	private final ListSignal<T> _input;
	private final SignalChooser<T> _chooser;
	@SuppressWarnings("unused") private final Object _refToAvoidGc;

	
	ListSignalModel(ListSignal<T> input, SignalChooser<T> chooser) {
		_input = input;
		_chooser = chooser;
		_refToAvoidGc = _SignalChoosers.receive(_input, new ModelChangeReceiver(_input));
	}
	
	
	private class ModelChangeReceiver implements ListChange.Visitor<T>, ListOfSignalsReceiver<T> {

		@SuppressWarnings("unused") private final WeakContract _refToAvoidGc2;

		private ModelChangeReceiver(ListSignal<T> input) {
			_refToAvoidGc2 = input.addListReceiverAsVisitor(this);
		}

		@Override public void elementAdded(final int index, T value) { fireIntervalAdded(ListSignalModel.this, index, index); }
		@Override public void elementRemoved(final int index, T value) { fireIntervalRemoved(ListSignalModel.this, index, index); }
		@Override public void elementReplaced(final int index, T oldValue, T newValue) { contentsChanged(index); }
		@Override public void elementMoved(final int index, final int newIndex, T newElement) { fireContentsChanged(ListSignalModel.this, index, newIndex); }

		@Override public void elementSignalChanged(final T value) { elementChanged(value); }
		@Override public SignalChooser<T> signalChooser() { return _chooser; }
	}
	
	@Override
	public int getSize() {
		Signal<Integer> size = _input.size();
		return size.currentValue();
	}
	
	@Override
	public T getElementAt(int index) {
		return _input.currentGet(index);
	}

	private void contentsChanged(final int index) {
		fireContentsChanged(ListSignalModel.this, index, index);
	}
	
	private void elementChanged(T element) {
		int i = 0;
		for (T candidate : _input) {  //Optimize
			if (candidate == element)
				contentsChanged(i);
			i++;
		}
	}	

	private static final long serialVersionUID = 1L;
}