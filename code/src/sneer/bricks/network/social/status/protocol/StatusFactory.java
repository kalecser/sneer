package sneer.bricks.network.social.status.protocol;

import java.util.Collection;

import sneer.foundation.brickness.Brick;

@Brick
public interface StatusFactory {

	String defaultValue();

	Collection<String> values();

	Status tupleFor(String statusName);

}
