package sneer.bricks.network.computers.ports.contacts.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.computers.ports.PortTuple;
import sneer.bricks.network.computers.ports.contacts.ContactPorts;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Producer;

public class ContactPortsImpl implements ContactPorts {

	private CacheMap<Seal, Register<Integer>> _portsBySeal = CacheMap.newInstance();

	WeakContract refToAvoidGc = my(TupleSpace.class).addSubscription(PortTuple.class, new Consumer<PortTuple>() { @Override public void consume(PortTuple tuple) {
		portRegisterGiven(tuple.publisher).setter().consume(tuple.port);
	}});
	
	@Override
	public Signal<Integer> portGiven(Seal seal) {
		return portRegisterGiven(seal).output();
	}

	private Register<Integer> portRegisterGiven(Seal seal) {
		return _portsBySeal.get(seal, new Producer<Register<Integer>>() { @Override public Register<Integer> produce() throws RuntimeException {
			return my(Signals.class).newRegister(0);
		}});
	}

}
