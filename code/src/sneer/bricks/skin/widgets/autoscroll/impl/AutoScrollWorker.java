package sneer.bricks.skin.widgets.autoscroll.impl;

import javax.swing.BoundedRangeModel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class AutoScrollWorker { 
	boolean wasAtEnd = false;
	int position;
	
	AutoScrollWorker(final JScrollPane subject) {
		subject.getVerticalScrollBar().getModel().addChangeListener(new ChangeListener() { @Override public void stateChanged(ChangeEvent e) {
			int newPosition = model(subject).getValue();
			
			if (isAtEnd(subject)) {
				wasAtEnd = true;
				position = newPosition;
				return;
			}

			if (!wasAtEnd) return;

			boolean userHasScrolled = newPosition != position && newPosition != 0; //For some obscure reason, swing sets the scroll model value to zero when showing the component for the first time. Klaus 2012
			if (userHasScrolled)
				wasAtEnd = false;
			else
				placeAtEnd(subject);
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