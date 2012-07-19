package sneer.bricks.pulp.notifiers.impl;

import static basis.environments.Environments.my;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.exceptionhandling.ExceptionHandler;
import sneer.bricks.pulp.notifiers.Notifier;
import sneer.bricks.pulp.notifiers.Source;
import basis.environments.Environments;
import basis.lang.Closure;
import basis.lang.Consumer;
import basis.lang.Producer;

class NotifierImpl<T> implements Notifier<T>, Source<T> {

	private static final WeakReference<?>[] RECEIVER_HOLDER_ARRAY_TYPE = new WeakReference[0];
	
	private final List<WeakReference<Consumer<? super T>>> _receivers = Collections.synchronizedList(new ArrayList<WeakReference<Consumer<? super T>>>());
	private final Producer<? extends T> _welcomeEventProducer;

	
	NotifierImpl(Producer<? extends T> welcomeEventProducer) {
		_welcomeEventProducer = welcomeEventProducer;
	}

	
	@Override
	public void notifyReceivers(T valueChange) {
		WeakReference<Consumer<? super T>>[] receivers = copyOfReceiversToAvoidConcurrentModificationAsResultOfNotifications();
		for (WeakReference<Consumer<? super T>> reference : receivers)
			notify(reference, valueChange);
	}

	
	@Override
	public WeakContract addPulseReceiver(final Runnable pulseReceiver) {
		return addReceiver(new Consumer<Object>() { @Override public void consume(Object ignored) {
			pulseReceiver.run();
		}});
	}

	
	@Override
	public WeakContract addReceiver(final Consumer<? super T> eventReceiver) {
		_receivers.add(new WeakReference<Consumer<? super T>>(eventReceiver));
		notifyCurrentValue(eventReceiver); //Fix: this is a potential inconsistency. The receiver might be notified of changes before the initial value. Reversing this line and the one above can cause the receiver to lose events. Some sort of synchronization has to happen here, without blocking too much.
		
		return new WeakContract() {
			@Override public void dispose() {
				_receivers.remove(eventReceiver); //Optimize consider a Set for when there is a great number of receivers.
			}
			@Override protected void finalize() {
				dispose();
			}
		};	
	}

	
	@Override
	public Source<T> output() {
		return this;
	}
	
	
	private WeakReference<Consumer<? super T>>[] copyOfReceiversToAvoidConcurrentModificationAsResultOfNotifications() {
		return (WeakReference<Consumer<? super T>>[]) _receivers.toArray(RECEIVER_HOLDER_ARRAY_TYPE);
	}

	
	private void notify(final WeakReference<Consumer<? super T>> ref, final T valueChange) {
		final Consumer<? super T> receiver = ref.get();
		if (receiver == null) {
			_receivers.remove(ref);
			return;
		}
		my(ExceptionHandler.class).shield(new Closure() { @Override public void run() {
			receiver.consume(valueChange);
		}});
	}

	
	private void notifyCurrentValue(final Consumer<? super T> receiver) {
		if (_welcomeEventProducer == null) return;
		Environments.my(ExceptionHandler.class).shield(new Closure() { @Override public void run() {
			receiver.consume(_welcomeEventProducer.produce());
		}});
	}

	
//	@Override
//	protected void finalize() throws Throwable {
//		ReceiverHolder<Consumer<T>>[] receivers = copyOfReceiversToAvoidConcurrentModificationAsResultOfNotifications();
//		if(receivers.length != 0) {
//			my(sneer.pulp.log.Logger.class).log(debugMessage(receivers));
//		}
//	}
//
//	private String debugMessage(ReceiverHolder<Consumer<T>>[] receivers) {
//		StringBuilder result = new StringBuilder();
//		result.append("Abstract notifier finalized.\n");
//		
//		for (ReceiverHolder<Consumer<T>> reference : receivers)
//			result.append("\tReceiver: " + reference._alias + "\n");
//		
//		return result.toString();
//	}
	
	
}