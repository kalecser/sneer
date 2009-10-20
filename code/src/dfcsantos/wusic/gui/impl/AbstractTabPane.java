package dfcsantos.wusic.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import dfcsantos.wusic.Wusic;

abstract class AbstractTabPane extends JPanel {

	protected static final Wusic Wusic = my(Wusic.class);

	private final JPanel _customPanel	= new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 3));
	private final JPanel _fixedPanel	= new JPanel(new GridLayout(2,1));

	protected AbstractTabPane() {
		super(new BorderLayout(3, 3));

		_fixedPanel.add(trackDisplay());
		_fixedPanel.add(controlPanel());

		add(_customPanel, BorderLayout.NORTH);
		add(_fixedPanel, BorderLayout.SOUTH);
	}

	abstract JLabel customTabLabel();

	abstract ControlPanel controlPanel();

	protected TrackDisplay trackDisplay() {
		return new TrackDisplay();
	}

	protected JPanel customPanel() {
		return _customPanel;
	}

}