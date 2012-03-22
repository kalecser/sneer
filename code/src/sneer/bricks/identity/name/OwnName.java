package sneer.bricks.identity.name;

import basis.brickness.Brick;
import sneer.bricks.network.social.attributes.Attribute;
import sneer.bricks.software.bricks.snapploader.Snapp;

@Brick
@Snapp
public interface OwnName extends Attribute<String> {

	String DEFAULT = "";

}
