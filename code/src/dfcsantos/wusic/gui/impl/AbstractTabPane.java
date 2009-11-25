package dfcsantos.wusic.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import dfcsantos.wusic.Wusic;
import dfcsantos.wusic.Wusic.OperatingMode;

abstract class AbstractTabPane extends JPanel {

	static final Wusic _controller = my(Wusic.class);

	private final JPanel _customPanel	= new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 3));
	private final JPanel _fixedPanel	= new JPanel(new GridLayout(2,1));

	private final JPanel _trackDisplay	= new TrackDisplay();
    private ControlPanel _controlPanel = newControlPanel();

	AbstractTabPane() {
		super(new BorderLayout(3, 3));

		_fixedPanel.add(_trackDisplay);
		_fixedPanel.add(_controlPanel);

		add(_customPanel, BorderLayout.NORTH);
		add(_fixedPanel, BorderLayout.SOUTH);
	}

	void updateComponents(OperatingMode operatingMode) {
		updateTrackDisplay(operatingMode);
		updateControlPanel(operatingMode);
	}

	private void updateTrackDisplay(OperatingMode operatingMode) {
		if (isMyOperatingMode(operatingMode))
			showTrackDisplay();
		else
			hideTrackDisplay();
	}

	private void showTrackDisplay() {
		_trackDisplay.setVisible(true);
	}

	private void hideTrackDisplay() {
		_trackDisplay.setVisible(false);
	}

	private void updateControlPanel(OperatingMode operatingMode) {
		_controlPanel.update(operatingMode);
	}

	JPanel customPanel() {
		return _customPanel;
	}

	abstract boolean isMyOperatingMode(OperatingMode operatingMode);

	abstract JLabel customTabLabel();

	abstract ControlPanel newControlPanel();

}