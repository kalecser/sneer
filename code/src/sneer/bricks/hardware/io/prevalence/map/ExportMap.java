package sneer.bricks.hardware.io.prevalence.map;

import sneer.foundation.brickness.Brick;

@Brick
public interface ExportMap {

	<T> T register(T object);
	boolean isRegistered(Object object);

	Object objectById(long id);
	long idByObject(Object object);

}
