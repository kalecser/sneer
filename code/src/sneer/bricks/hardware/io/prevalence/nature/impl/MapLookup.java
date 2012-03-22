package sneer.bricks.hardware.io.prevalence.nature.impl;

import static basis.environments.Environments.my;
import basis.lang.Producer;
import sneer.bricks.hardware.io.prevalence.map.PrevalenceMap;


class MapLookup implements Producer<Object> {
	
	private static PrevalenceMap PrevalenceMap = my(PrevalenceMap.class);

	
	MapLookup(Object delegate) {
		_id = PrevalenceMap.marshal(delegate);
	}


	private final long _id;
	

	@Override
	public Object produce() {
		return PrevalenceMap.unmarshal(_id);
	}


}
