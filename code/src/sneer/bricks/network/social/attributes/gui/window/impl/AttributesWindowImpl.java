package sneer.bricks.network.social.attributes.gui.window.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.Dimension;

import javax.swing.JFrame;

import sneer.bricks.network.social.attributes.Attribute;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.network.social.attributes.gui.AttributeListPanels;
import sneer.bricks.network.social.attributes.gui.AttributePanelElement;
import sneer.bricks.network.social.attributes.gui.window.AttributesWindow;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.collections.CollectionSignal;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Functor;
import sneer.foundation.lang.PickyConsumer;

class AttributesWindowImpl implements AttributesWindow {

	private static final Signal<String> TITLE = my(Signals.class).constant("Attributes Settings");

	private JFrame _frame;

    public AttributesWindowImpl() {
		my(MainMenu.class).menu().addAction(50, "Settings", new Closure() { @Override synchronized public void run() {
			if (_frame == null) _frame = initFrame();
			_frame.setVisible(true);
		}});

	}

	private JFrame initFrame() {
		JFrame result = my(ReactiveWidgetFactory.class).newFrame(TITLE).getMainWidget();
		result.add(my(AttributeListPanels.class).produce(attributes()));
		result.setLocationRelativeTo(null);
		result.setMinimumSize(new Dimension(365, 80));
	    result.setResizable(false);
		result.pack();

		return result;
	}

	private CollectionSignal<AttributePanelElement> attributes() {
		return my(CollectionSignals.class).adapt(
			my(Attributes.class).all(),
			new Functor<Class<? extends Attribute<?>>, AttributePanelElement>() { @Override public AttributePanelElement evaluate(final Class<? extends Attribute<?>> attribute) {
				return new AttributePanelElement() {

					@Override
					public PickyConsumer<String> valueSetter() {
						return my(Attributes.class).myAttributeSetter((Class<? extends Attribute<String>>) attribute);
					}

					@Override
					public Signal<String> value() {
						return my(Attributes.class).myAttributeValue((Class<? extends Attribute<String>>) attribute);
					}

					@Override
					public String name() {
						return attribute.getSimpleName();
					}
				};
			}
		});
	}

}
