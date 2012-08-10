package sneer.bricks.skin.widgets.reactive.autoscroll.impl;

import java.awt.event.FocusAdapter;

import javax.swing.JScrollPane;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.notifiers.Source;
import sneer.bricks.skin.widgets.reactive.autoscroll.ReactiveAutoScroll;
import basis.lang.Consumer;

public class ReactiveAutoScrollImpl implements ReactiveAutoScroll {

	@Override
	public <T> JScrollPane create(Source<T> eventSource, final Consumer<T> receiver) {
		final JScrollPane result = new JScrollPane();
		WeakContract reception = eventSource.addReceiver(new Consumer<T>() {  @Override public void consume(final T change) {
			receiver.consume(change);
		}});
		new AutoScroll(result);
		hackToHoldReceivers(result, reception);
		return result;
	}

	private void hackToHoldReceivers(JScrollPane scroll, final WeakContract contract) {
		scroll.addFocusListener(new FocusAdapter(){
			@SuppressWarnings({ "unused" })
			WeakContract _refToAvoidGc = contract;
		});
	}
}
