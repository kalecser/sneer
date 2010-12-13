package sneer.bricks.network.social.attributes;

import static sneer.foundation.environments.Environments.my;

public abstract class AbstractAttribute<T> implements Attribute<T> {

	public AbstractAttribute() {
		my(Attributes.class).registerAttribute(this);
	}

}
