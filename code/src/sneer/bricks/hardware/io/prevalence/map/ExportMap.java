package sneer.bricks.hardware.io.prevalence.map;

import sneer.foundation.brickness.Brick;

@Brick
public interface ExportMap {

	<T> T register(T object);
	boolean isRegistered(Object object);

	long marshal(Object object);
	Object unmarshal(long id);

	void marshal(Object[] array);
	void unmarshal(Object[] array);

}
