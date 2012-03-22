package sneer.bricks.network.social.attributes.gui.impl;

import static basis.environments.Environments.my;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import sneer.bricks.network.social.attributes.gui.AttributePanelElement;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.bricks.skin.widgets.reactive.TextWidget;

class AttributePanel extends JPanel {
 
	private final JLabel _name;
	private final TextWidget<JTextField> _value;

	AttributePanel(AttributePanelElement attribute) {
		_name = new JLabel(attribute.name());
		_name.setAlignmentX(LEFT_ALIGNMENT);
		add(_name);

		_value = my(ReactiveWidgetFactory.class).newTextField(
			attribute.value(), attribute.valueSetter()
		);
		JTextField valueTextFiled = _value.getMainWidget();
		valueTextFiled.setPreferredSize(new Dimension(90, 18));
		valueTextFiled.setAlignmentX(LEFT_ALIGNMENT);
		add(valueTextFiled);

	}

}
