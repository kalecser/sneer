package sneer.bricks.pulp.notifiers.pulsers.impl;

import java.util.ArrayList;
import java.util.List;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.notifiers.pulsers.Pulser;

class UmbrellaContract implements WeakContract {

	
	private final List<WeakContract> _subContracts;

	
	UmbrellaContract(Runnable receiver, Pulser... sources) {
		_subContracts = new ArrayList<WeakContract>(sources.length);
		
		for (Pulser source : sources)
			_subContracts.add(source.addPulseReceiver(receiver));
	}


	@Override
	public void dispose() {
		for (WeakContract subContract : _subContracts)
			subContract.dispose();
	}

	@Override
	protected void finalize() {
		dispose();
	}
	
}
