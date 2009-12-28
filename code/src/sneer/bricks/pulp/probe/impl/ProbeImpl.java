/**
 * 
 */
package sneer.bricks.pulp.probe.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.distribution.filtering.TupleFilterManager;
import sneer.bricks.pulp.keymanager.Seal;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.tuples.Tuple;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.lang.Consumer;

final class ProbeImpl implements Consumer<Tuple> {

	private final TupleSpace _tuples = my(TupleSpace.class);
	private final Seals _keyManager = my(Seals.class);
	private final TupleFilterManager _filter = my(TupleFilterManager.class);

	
	private final Contact _contact;
	private Seal _contactsSeal;
	
	private final Object _isConnectedMonitor = new Object();
	private boolean _isConnected = false;
	final SchedulerImpl _scheduler = new SchedulerImpl();

	@SuppressWarnings("unused") private final Object _referenceToAvoidGc;
	private WeakContract _tupleSpaceContract;

	ProbeImpl(Contact contact, Signal<Boolean> isConnectedSignal) {
		_contact = contact;
		_referenceToAvoidGc = isConnectedSignal.addReceiver(new Consumer<Boolean>(){ @Override public void consume(Boolean isConnected) {
			dealWithIsConnected(isConnected);
		}});
	}

	private void dealWithIsConnected(boolean isConnected) {
		synchronized (_isConnectedMonitor) {
			boolean wasConnected = _isConnected;
			_isConnected = isConnected;

			if (isConnected) {
				_tupleSpaceContract = _tuples.addSubscription(Tuple.class, this);
			} else if (wasConnected) {
				_tupleSpaceContract.dispose();
				_scheduler.drain();
			}
		}
	}


	@Override
	public void consume(Tuple tuple) {
		if (!isClearToSend(tuple)) return;
		
		synchronized (_isConnectedMonitor) {
			if (!_isConnected)	return;
			_scheduler.add(tuple);
		}
	}

	private boolean isClearToSend(Tuple tuple) {
		initContactsPKIfNecessary();
		if (_contactsSeal == null) return false;

		if (!_filter.canBePublished(tuple)) return false;
		if (!isCorrectAddressee(tuple)) return false;
		if (isEcho(tuple)) return false;
		
		return true;
	}

	
	private boolean isCorrectAddressee(Tuple tuple) {
		return (tuple.addressee == null || tuple.addressee.equals(_contactsSeal));
	}

	
	private boolean isEcho(Tuple tuple) {
		return _contactsSeal.equals(tuple.publisher);
	}

	private void initContactsPKIfNecessary() {
		if (_contactsSeal != null) return;
		_contactsSeal = _keyManager.sealGiven(_contact);
	}

}