package sneer.bricks.network.social.attributes.gui.impl;

import static sneer.foundation.environments.Environments.my;

import javax.swing.JFrame;

import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.foundation.lang.Closure;

class AttributesWindowImpl {

	private static final Attributes _controller = my(Attributes.class);

	private static final Signal<String> TITLE = my(Signals.class).constant("Attributes Panel");

	private JFrame _frame;

    public AttributesWindowImpl() {
		my(MainMenu.class).addAction(50, "Attributes", new Closure() { @Override synchronized public void run() {
			if (_frame == null) _frame = initFrame();
			_frame.setVisible(true);
		}});

	}

	private JFrame initFrame() {
		JFrame result = my(ReactiveWidgetFactory.class).newFrame(TITLE).getMainWidget();
		result.add(new MainPanel(_controller));
		result.setLocationRelativeTo(null);
		result.pack();

		return result;
	}

}
