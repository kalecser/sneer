package sneer.bricks.network.social.attributes.gui.window.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.Dimension;

import javax.swing.JFrame;

import sneer.bricks.network.social.attributes.gui.AttributeListPanels;
import sneer.bricks.network.social.attributes.gui.AttributePanelElement;
import sneer.bricks.network.social.attributes.gui.window.AttributesWindow;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.foundation.lang.Closure;

class AttributesWindowImpl implements AttributesWindow {

	private static final Signal<String> TITLE = my(Signals.class).constant("Attributes Panel");

	private JFrame _frame;

    public AttributesWindowImpl() {
		my(MainMenu.class).addAction(50, "Preferences", new Closure() { @Override synchronized public void run() {
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

	private SetSignal<AttributePanelElement> attributes() {
//		return my(CollectionUtils.class).map(
//			my(Attributes.class).all().currentElements(),
//			new Functor<Class<? extends Attribute<?>>, AttributePanelElement>() { @Override public AttributePanelElement evaluate(final Class<? extends Attribute<?>> attribute) {
//				return new AttributePanelElement() {
//
//					@Override
//					public PickyConsumer<String> valueSetter() {
//						return my(Attributes.class).myAttributeSetter((Class<? extends Attribute<String>>) attribute);
//					}
//
//					@Override
//					public Signal<String> value() {
//						return my(Attributes.class).myAttributeValue((Class<? extends Attribute<String>>) attribute);
//					}
//
//					@Override
//					public String name() {
//						return attribute.toString();
//					}
//				};
//			}
//		});
		return null;
	}

}
