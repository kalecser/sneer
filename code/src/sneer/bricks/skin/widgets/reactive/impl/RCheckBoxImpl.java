package sneer.bricks.skin.widgets.reactive.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import sneer.bricks.hardware.gui.guithread.GuiThread;
import sneer.bricks.hardware.ram.ref.weak.keeper.WeakReferenceKeeper;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.skin.widgets.reactive.ToggleButtonWidget;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.PickyConsumer;

class RCheckBoxImpl extends RPanel<JCheckBox> implements ToggleButtonWidget<JCheckBox> {

	private final JCheckBox _component = new JCheckBox();
	private final Signal<Boolean> _source;
	private final Consumer<Boolean> _setter;
	private final Runnable _refreshOperations;

	@SuppressWarnings("unused")
	private final Object _toAvoidGc;

	RCheckBoxImpl(Signal<Boolean> source, Consumer<Boolean> setter, Runnable cascadeRefreshOperations) {
		_source = source;
		_setter = setter;
		_refreshOperations = cascadeRefreshOperations;

		initComponent();
		initActionListerner();

		_toAvoidGc = _source.addReceiver(new Consumer<Boolean>() { @Override public void consume(final Boolean isSelected) {
			my(GuiThread.class).invokeAndWaitForWussies(new Closure() { @Override public void run() {
				_component.setSelected(isSelected);
				refreshComponent();
			}});
		}});
	}

	@Override
	public Signal<Boolean> output() {
		return _source;
	}

	@Override
	public PickyConsumer<Boolean> setter() {
		return _setter;
	}

	@Override
	public JCheckBox getMainWidget() {
		return component();
	}

	private JCheckBox component() {
		return my(WeakReferenceKeeper.class).keep(_component, this);
	}

	private void initComponent() {
		if (_setter == null)
			_component.setEnabled(false);

		setOpaque(false);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints(
			0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0
		);
		add(_component, c);
	}

	private void initActionListerner() {
		_component.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) {
			commitChanges();
		}});
	}

	private void commitChanges() {
		my(GuiThread.class).assertInGuiThread();

		boolean isSelected = _component.isSelected();
		if (isSelected == _source.currentValue()) return;

		_setter.consume(isSelected);
		refreshComponent();
	}

	private void refreshComponent() {
		_component.revalidate();
		if (_refreshOperations != null) _refreshOperations.run();
	}
}
