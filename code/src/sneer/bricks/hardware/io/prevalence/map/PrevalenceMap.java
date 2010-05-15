package sneer.bricks.hardware.io.prevalence.map;

import sneer.foundation.brickness.Brick;

@Brick
public interface PrevalenceMap {

	void register(Object object);
	boolean isRegistered(Object object);

	long marshal(Object object);
	Object unmarshal(long id);

	void marshal(Object[] array);
	void unmarshal(Object[] array);

}
