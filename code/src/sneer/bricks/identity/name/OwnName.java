package sneer.bricks.identity.name;

import sneer.bricks.network.social.attributes.Attribute;
import sneer.bricks.software.bricks.snappstarter.Snapp;
import sneer.foundation.brickness.Brick;

@Brick
@Snapp
public interface OwnName extends Attribute<String> {

	String DEFAULT = "";

}
