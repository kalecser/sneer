package sneer.bricks.hardware.io.prevalence.map;

import basis.brickness.Brick;

@Brick
public interface PrevalenceMap {

	void register(Object object);
	boolean isRegistered(Object object);
	boolean requiresRegistration(Object object);

	long marshal(Object object);
	Object unmarshal(long id);

	Object[] marshal(Object[] array);
	Object[] unmarshal(Object[] array);

}
