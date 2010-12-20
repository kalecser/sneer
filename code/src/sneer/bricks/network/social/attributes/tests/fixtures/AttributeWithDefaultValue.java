package sneer.bricks.network.social.attributes.tests.fixtures;

import sneer.bricks.network.social.attributes.Attribute;
import sneer.bricks.software.bricks.snappstarter.Snapp;
import sneer.foundation.brickness.Brick;

@Snapp
@Brick
public interface AttributeWithDefaultValue extends Attribute<String> {
	
	static String DEFAULT = "Hello";
	
}
