package sneer.bricks.pulp.reactive.gates.buffers.assync.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.ram.ref.weak.keeper.WeakReferenceKeeper;
import sneer.bricks.pulp.events.EventNotifier;
import sneer.bricks.pulp.events.EventNotifiers;
import sneer.bricks.pulp.events.EventSource;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;


class AssynchronousBufferImpl<T> {

	private final BlockingQueue<T> _buffer = new LinkedBlockingQueue<T>();
	private final EventNotifier<T> _delegate = my(EventNotifiers.class).newInstance();
	private Thread _daemon;
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGc;

	
	AssynchronousBufferImpl(EventSource<T> input, String threadName) {
		my(Threads.class).startDaemon(threadName, new Closure() { @Override public void run() {
			_daemon = Thread.currentThread();
			
			try {
				while (true)
					_delegate.notifyReceivers(_buffer.take());
			} catch (InterruptedException expected) {
				//finalizer() will interrupt this thread.
			}

		}});
		
		
		_refToAvoidGc = input.addReceiver(new Consumer<T>() { @Override public void consume(T value) {
			_buffer.add(value);
		}});
	}


	EventSource<T> output() {
		return my(WeakReferenceKeeper.class).keep(_delegate.output(), finalizer());
	}


	private Object finalizer() {
		return new Object() {
			@Override protected void finalize() {
				_daemon.interrupt();
			}
		};
	}
	
}
