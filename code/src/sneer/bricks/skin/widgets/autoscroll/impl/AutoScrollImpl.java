package sneer.bricks.skin.widgets.autoscroll.impl;

import javax.swing.JScrollPane;

import sneer.bricks.skin.widgets.autoscroll.AutoScroll;

public class AutoScrollImpl implements AutoScroll {

	@Override
	public void autoscroll(JScrollPane subject) {
		new AutoScrollWorker(subject);
	}

}
