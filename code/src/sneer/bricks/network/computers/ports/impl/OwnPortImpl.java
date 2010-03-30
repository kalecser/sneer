package sneer.bricks.network.computers.ports.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.network.computers.ports.OwnPort;
import sneer.bricks.network.computers.ports.PortTuple;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.bricks.statestore.BrickStateStore;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.PickyConsumer;
import sneer.foundation.lang.exceptions.NotImplementedYet;
import sneer.foundation.lang.exceptions.Refusal;

class OwnPortImpl implements OwnPort {

	private final BrickStateStore _store = my(BrickStateStore.class);
	private final PortNumberRegister _register = new PortNumberRegister(0);
	
	@SuppressWarnings("unused")
	private Object _refToAvoidGc;
	
	OwnPortImpl(){
		my(TupleSpace.class).keep(PortTuple.class);

		restore();
		_refToAvoidGc = port().addReceiver(new Consumer<Integer>(){ @Override public void consume(Integer port) {
			save(port);
		}});
	}
	
	@Override
	public Signal<Integer> port() {
		return _register.output();
	}
	
	@Override
	public PickyConsumer<Integer> portSetter() {
		return new PickyConsumer<Integer>() { @Override public void consume(Integer value) throws Refusal {
			if (_register.output().currentValue().equals(value)) return;
			_register.setter().consume(value);
			my(TupleSpace.class).acquire(new PortTuple(value));
		}};
	}

	private void save(Integer port) {
		_store.writeObjectFor(OwnPort.class, port);
	}
	
	private void restore() {
		Integer restoredPort = (Integer) _store.readObjectFor(OwnPort.class, getClass().getClassLoader());
		if(restoredPort!=null)
			try {
				portSetter().consume(restoredPort);
			} catch (Refusal e) {
				throw new NotImplementedYet(e); // Fix Handle this exception.
			}
	}
}