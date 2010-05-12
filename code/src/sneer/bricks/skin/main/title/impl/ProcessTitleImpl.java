package sneer.bricks.skin.main.title.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.identity.name.OwnName;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.main.title.ProcessTitle;
import sneer.foundation.lang.Functor;

public class ProcessTitleImpl implements ProcessTitle {

	@Override
	public Signal<String> title() {
		return my(Signals.class).adapt(
		my(Attributes.class).myAttributeValue(OwnName.class), 
		new Functor<String, String>(){	@Override public String evaluate(String ownName) {
			return "Sneer - " + ownName;
		}});
	}

}
