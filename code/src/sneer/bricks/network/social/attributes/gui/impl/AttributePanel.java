package sneer.bricks.network.social.attributes.gui.impl;

import static sneer.foundation.environments.Environments.my;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import sneer.bricks.network.social.attributes.Attribute;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.bricks.skin.widgets.reactive.TextWidget;

class AttributePanel extends JPanel {
 
	private final TextWidget<JLabel> _name;
	private final TextWidget<JTextField> _value;

	AttributePanel(Class<? extends Attribute<?>> attribute) {
		final Attributes attributes = my(Attributes.class);

		_name = my(ReactiveWidgetFactory.class).newLabel(my(Signals.class).constant(attribute.toString()));
		add(_name.getMainWidget());

		_value = my(ReactiveWidgetFactory.class).newTextField(
			attributes.myAttributeValue((Class<? extends Attribute<Object>>) attribute),
			attributes.myAttributeSetter((Class<? extends Attribute<Object>>) attribute)
		);
		add(_value.getMainWidget());

	}

}
