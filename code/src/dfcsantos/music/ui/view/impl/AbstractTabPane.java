package dfcsantos.music.ui.view.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import dfcsantos.music.Wusic;
import dfcsantos.music.Wusic.OperatingMode;

abstract class AbstractTabPane extends JPanel {

	static final Wusic _controller = my(Wusic.class);

	private final JPanel _customPanel	= new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 5));
	private final JPanel _trackDisplay	= new TrackDisplay();
    private final ControlPanel _controlPanel = newControlPanel();

	AbstractTabPane() {
		super(new GridLayout(3, 1, 0, 5));

		add(_customPanel);
		add(_trackDisplay);
		add(_controlPanel);
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