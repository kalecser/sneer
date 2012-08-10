package sneer.bricks.snapps.chat.gui.panels.impl;

import static basis.environments.Environments.my;

import javax.swing.JTextPane;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;

import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;

class JTextPaneWithoutAutoScrollOnEdit extends JTextPane {
	
	JTextPaneWithoutAutoScrollOnEdit(){
		preventAutoScrollingOnEdit();
	}

	private void preventAutoScrollingOnEdit() {
		Caret caret = this.getCaret();
		if (!(caret instanceof DefaultCaret)) {
			my(BlinkingLights.class).turnOn(LightType.WARNING, 
					"Strange scrolling behaviour", 
					"Your scrolling windows might auto-scroll in strange moments.\n\n" +
					"(JTextPane default caret class has changed. Used to be DefaultCaret.)");
			return;
		}
		((DefaultCaret)caret).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
	}
}
