package sneer.bricks.hardware.io.prevalence.nature.impl;


class MapLookup extends BuildingTransaction {
		
	MapLookup(Object delegate) {
		_id = PrevalenceMap.marshal(delegate);
	}


	private final long _id;
	

	@Override
	protected Object execute() {
		return PrevalenceMap.unmarshal(_id);
	}


}
