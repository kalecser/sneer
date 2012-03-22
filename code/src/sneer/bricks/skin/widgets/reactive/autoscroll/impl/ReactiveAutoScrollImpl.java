package sneer.bricks.skin.widgets.reactive.autoscroll.impl;

import static basis.environments.Environments.my;

import java.awt.event.FocusAdapter;

import javax.swing.JScrollPane;

import basis.lang.Closure;
import basis.lang.Consumer;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.notifiers.Source;
import sneer.bricks.skin.widgets.autoscroll.AutoScroll;
import sneer.bricks.skin.widgets.reactive.autoscroll.ReactiveAutoScroll;

public class ReactiveAutoScrollImpl implements ReactiveAutoScroll {

	@Override
	public <T> JScrollPane create(Source<T> eventSource, final Consumer<T> receiver) {
		
		final JScrollPane result = new JScrollPane();
		
		WeakContract reception = eventSource.addReceiver(new Consumer<T>() {  @Override public void consume(final T change) {
			my(AutoScroll.class).runWithAutoscroll(result, new Closure() {  @Override public void run() {
				receiver.consume(change);
			}});
		}});
		
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
