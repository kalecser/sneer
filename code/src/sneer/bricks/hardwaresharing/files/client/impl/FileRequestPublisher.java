package sneer.bricks.hardwaresharing.files.client.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardwaresharing.files.protocol.FileRequest;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.tuples.TupleSpace;

class FileRequestPublisher {

	private final static Deque<Sneer1024> _pendingRequests = new LinkedList<Sneer1024>();
	private final static Map<Sneer1024, String> _debugInfoByRequest = new ConcurrentHashMap<Sneer1024, String>();
	
	@SuppressWarnings("unused") private final static WeakContract _timerContract;


	
	static {
		_timerContract = my(Timer.class).wakeUpNowAndEvery(1000 * 60 * 15, new Runnable() { @Override public void run() {
			publishPendingRequest();
		}});
	}
	
	
	synchronized
	static void startRequesting(Sneer1024 hashOfContents, String debugInfo) {
		if (_pendingRequests.contains(hashOfContents))
			return;
		_debugInfoByRequest.put(hashOfContents, debugInfo);
		takeTurnToPublish(hashOfContents);
	}

	
	synchronized
	static void stopRequesting(Sneer1024 hashOfContents) {
		_pendingRequests.remove(hashOfContents);
		_debugInfoByRequest.remove(hashOfContents);
	}

	
	synchronized
	private static void publishPendingRequest() {
		if (_pendingRequests.isEmpty()) return;
		takeTurnToPublish(_pendingRequests.removeFirst());
	}

	
	private static void takeTurnToPublish(Sneer1024 hashOfContents) {
		my(TupleSpace.class).publish(new FileRequest(hashOfContents, _debugInfoByRequest.get(hashOfContents)));
		_pendingRequests.addLast(hashOfContents);
	}
	
}
