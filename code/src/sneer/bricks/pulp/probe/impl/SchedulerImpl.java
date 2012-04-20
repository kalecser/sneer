package sneer.bricks.pulp.probe.impl;

import static basis.environments.Environments.my;

import java.util.LinkedList;
import java.util.List;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.network.computers.connections.ByteConnection.PacketScheduler;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.serialization.Serializer;

class SchedulerImpl implements PacketScheduler {

	private final Serializer _serializer = my(Serializer.class);

	private final List<Tuple> _toSend = new LinkedList<Tuple>();
	private boolean _wasDrained = false;
	private int _lastTupleSent;

	@Override
	public byte[] highestPriorityPacketToSend() {
		while (true) {
			Tuple tuple = highestPriorityTupleToSend();
			try {
				return _serializer.serialize(tuple); //Optimize: Use same serialized form of this tuple for all interested contacts.
			} catch (RuntimeException e) {
				my(BlinkingLights.class).turnOn(LightType.ERROR, "Error Serializing Tuple to Send", "Report this to your sovereign buddy.", e, 20000);
				previousPacketWasSent();
			}
		}
	}

	synchronized private Tuple highestPriorityTupleToSend() {
		while (_toSend.isEmpty())
			my(Threads.class).waitWithoutInterruptions(this);

		_lastTupleSent = _toSend.size() - 1;
		return _toSend.get(_lastTupleSent);
	}

	@Override
	public synchronized void previousPacketWasSent() {
		if (_wasDrained) return;
		_toSend.remove(_lastTupleSent);
	}

	synchronized void drain() {
		_wasDrained = true;
		_toSend.clear();
	}

	synchronized void add(Tuple tuple) {
		_wasDrained = false;
		_toSend.add(tuple);
		notify();
	}
}