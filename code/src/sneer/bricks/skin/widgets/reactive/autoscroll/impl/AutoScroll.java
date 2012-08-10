package sneer.bricks.skin.widgets.reactive.autoscroll.impl;

import javax.swing.BoundedRangeModel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class AutoScroll{ 
	boolean atEnd = false;
	int value;
	
	AutoScroll(final JScrollPane subject) {
		subject.getVerticalScrollBar().getModel().addChangeListener(new ChangeListener() { @Override public void stateChanged(ChangeEvent e) {
			int newValue = model(subject).getValue();
			
			if (newValue == 0 && atEnd) { //Ignore silly swing event that sets scroll model value to zero when showing component.
				placeAtEnd(subject);
				return;
			}
			
			if (isAtEnd(subject)) {
				atEnd = true;
				value = newValue;
				return;
			}
			
			if (value == newValue)
				placeAtEnd(subject);
			else
				atEnd = false;
		}});

		placeAtEnd(subject);	
	}
	
	private void placeAtEnd(final JScrollPane scrollPane) {
		BoundedRangeModel model = model(scrollPane);
		model.setValue(model.getMaximum() - model.getExtent());
		if(!isAtEnd(scrollPane)) throw new IllegalStateException();
	}
	
	private boolean isAtEnd(final JScrollPane scrollPane) {
		BoundedRangeModel model = model(scrollPane);
		return model.getValue() + model.getExtent() == model.getMaximum();
	}		
	private BoundedRangeModel model(JScrollPane scrollPane) {
		return scrollPane.getVerticalScrollBar().getModel();
	}
}