package sneer.bricks.skin.widgets.reactive.impl;

import javax.swing.ComboBoxModel;

import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.pulp.reactive.signalchooser.SignalChooser;

class ComboBoxSignalModel<T> extends ListSignalModel<T> implements ComboBoxModel {


	@Override
	public Object getSelectedItem() {
		return _selectedItem;
	}

	
	@Override
	public void setSelectedItem(Object newItem) {
		if (_selectedItem == null) _selectedItem = new Object();

		if (_selectedItem.equals(newItem)) return;
		_selectedItem = newItem;
		
		fireContentsChanged(this, -1, -1);
	}


	ComboBoxSignalModel(ListSignal<T> input, SignalChooser<T> chooser) {
		super(input, chooser);
	}

	
	private Object _selectedItem;
	private static final long serialVersionUID = 1L;
}
