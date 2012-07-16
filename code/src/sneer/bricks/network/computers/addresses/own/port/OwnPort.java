package sneer.bricks.network.computers.addresses.own.port;

import basis.brickness.Brick;
import sneer.bricks.network.social.attributes.Attribute;
import sneer.bricks.software.bricks.snapploader.Snapp;

@Snapp
@Brick
public interface OwnPort extends Attribute<Integer> {
	
	Integer NOT_YET_SET = -1;
	
	Integer DEFAULT = NOT_YET_SET; 

}
