package dfcsantos.wusic.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.foundation.lang.Consumer;

import dfcsantos.wusic.Wusic;
import dfcsantos.wusic.Wusic.OperatingMode;

abstract class AbstractTabPane extends JPanel {

	static final Wusic _controller = my(Wusic.class);

	private final JPanel _customPanel	= new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 3));
	private final JPanel _fixedPanel	= new JPanel(new GridLayout(2,1));

	private final JPanel _trackDisplay	= new TrackDisplay();

	@SuppressWarnings("unused") private final WeakContract _toAvoidGC;

	AbstractTabPane() {
		super(new BorderLayout(3, 3));

		_fixedPanel.add(_trackDisplay);
		_fixedPanel.add(controlPanel());

		add(_customPanel, BorderLayout.NORTH);
		add(_fixedPanel, BorderLayout.SOUTH);

		_toAvoidGC = _controller.operatingMode().addReceiver(new Consumer<OperatingMode>() { @Override public void consume(OperatingMode operatingMode) {
			if (operatingMode.equals(panelOperatingMode()))
				showTrackDisplay();
			else
				hideTrackDisplay();
		}});
	}

	private void showTrackDisplay() {
		_trackDisplay.setVisible(true);
	}

	private void hideTrackDisplay() {
		_trackDisplay.setVisible(false);
	}

	JPanel customPanel() {
		return _customPanel;
	}

	abstract OperatingMode panelOperatingMode();

	abstract JLabel customTabLabel();

	abstract ControlPanel controlPanel();

}