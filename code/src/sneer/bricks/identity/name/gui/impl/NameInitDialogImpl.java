package sneer.bricks.identity.name.gui.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.identity.name.OwnName;
import sneer.bricks.identity.name.gui.NameInitDialog;
import sneer.bricks.network.social.attributes.Attributes;

public class NameInitDialogImpl implements NameInitDialog {

	{
		if (ownName().isEmpty()) initName();
	}


	private void initName() {
		//throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	
	private String ownName() {
		return my(Attributes.class).myAttributeValue(OwnName.class).currentValue();
	}
	
}
