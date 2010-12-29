package sneer.bricks.network.social.attributes.tests.fixtures;

import sneer.bricks.network.social.attributes.Attribute;

public interface AttributeWithDefaultValue extends Attribute<String> {
	
	static String DEFAULT = "Hello";
	
}
