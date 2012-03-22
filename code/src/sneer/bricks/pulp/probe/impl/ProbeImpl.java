/**
 * 
 */
package sneer.bricks.pulp.probe.impl;

import static basis.environments.Environments.my;
import basis.lang.Consumer;
import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.distribution.filtering.TupleFilterManager;
import sneer.bricks.pulp.reactive.Signal;

final class ProbeImpl implements Consumer<Tuple> {

	private final TupleSpace _tuples = my(TupleSpace.class);
	private final ContactSeals _keyManager = my(ContactSeals.class);
	private final TupleFilterManager _filter = my(TupleFilterManager.class);

	
	private final Contact _contact;
	
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
		Seal seal = contactsSeal();
		if (seal == null) return false;

		if (!_filter.canBePublished(tuple)) return false;
		if (!isCorrectAddressee(tuple, seal)) return false;
		if (isEcho(tuple, seal)) return false;
		
		return true;
	}

	
	private Seal contactsSeal() {
		return _keyManager.sealGiven(_contact).currentValue();
	}

	
	private boolean isCorrectAddressee(Tuple tuple, Seal seal) {
		return (tuple.addressee == null || tuple.addressee.equals(seal));
	}

	
	private boolean isEcho(Tuple tuple, Seal seal) {
		return seal.equals(tuple.publisher);
	}

}