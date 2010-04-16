package sneer.bricks.skin.widgets.reactive.impl;

import static sneer.foundation.environments.Environments.my;

import javax.swing.JProgressBar;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.gui.guithread.GuiThread;
import sneer.bricks.hardware.ram.ref.weak.keeper.WeakReferenceKeeper;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.skin.widgets.reactive.Widget;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;

class RProgressBarImpl extends RPanel<JProgressBar> implements Widget<JProgressBar> {

	private final JProgressBar _component = new JProgressBar(0, 100);
	private final Signal<Integer> _source;

	@SuppressWarnings("unused") private final WeakContract _toAvoidGc;

	RProgressBarImpl(Signal<Integer> source) {
		_source = source;

		_component.setStringPainted(true);
		
		_toAvoidGc = _source.addReceiver(new Consumer<Integer>() { @Override public void consume(final Integer progress) {
			my(GuiThread.class).invokeAndWaitForWussies(new Closure() { @Override public void run() {
				_component.setValue(Math.min(100, progress));
			}});
		}});
	}

	@Override
	public JProgressBar getMainWidget() {
		return component();
	}

	private JProgressBar component() {
		return my(WeakReferenceKeeper.class).keep(_component, this);
	}

}
