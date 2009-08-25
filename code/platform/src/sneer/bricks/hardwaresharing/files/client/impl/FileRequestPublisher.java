package sneer.bricks.hardwaresharing.files.client.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.Deque;
import java.util.LinkedList;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Steppable;
import sneer.bricks.hardwaresharing.files.protocol.FileRequest;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.tuples.TupleSpace;

class FileRequestPublisher {

	private final static Deque<Sneer1024> _pendingRequests = new LinkedList<Sneer1024>();
	
	@SuppressWarnings("unused") private final static WeakContract _timerContract;

	
	static {
		_timerContract = my(Timer.class).wakeUpNowAndEvery(10000, new Steppable() { @Override public void step() {
			publishPendingRequest();
		}});
	}
	
	
	static void startPublishing(Sneer1024 hashOfContents) {
		publish(hashOfContents);
		synchronized (_pendingRequests) {
			_pendingRequests.addLast(hashOfContents);
		}
	}

	
	static void stopPublishing(Sneer1024 hashOfContents) {
		synchronized (_pendingRequests) {
			_pendingRequests.remove(hashOfContents);
		}
	}

	
	private static void publishPendingRequest() {
		Sneer1024 hashOfContents;
		synchronized (_pendingRequests) {
			if (_pendingRequests.isEmpty()) return;
			hashOfContents = _pendingRequests.removeFirst();
			_pendingRequests.addLast(hashOfContents);
		}
		publish(hashOfContents);
	}

	
	private static void publish(Sneer1024 hashOfContents) {
		my(TupleSpace.class).publish(new FileRequest(hashOfContents));
	}
	
}
