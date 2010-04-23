package sneer.bricks.hardware.io.prevalence.map;

import sneer.foundation.brickness.Brick;

@Brick
public interface PrevalentMap {

	<T> T register(T object);

	long idByObject(Object object);

	Object objectById(long id);

	boolean isRegistered(Object object);

}
