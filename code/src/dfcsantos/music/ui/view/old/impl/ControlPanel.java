package dfcsantos.music.ui.view.old.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.foundation.lang.Consumer;
import dfcsantos.music.Music;
import dfcsantos.music.Music.OperatingMode;

abstract class ControlPanel extends JPanel {

	private static final int MAX_VOLUME = 10;

	private static final Music _controller	= my(Music.class);

	private static final String RESUME_ICON	= "\u25BA";
	private static final String PAUSE_ICON	= "\u2161";
//	private static final String BACK_ICON	= "<<";
	private static final String SKIP_ICON	= ">>";
	private static final String STOP_ICON	= "\u25A0";

	private final JButton _pauseResume		= new JButton();
//	private final JButton _back				= new JButton();
	private final JButton _skip				= new JButton();
	private final JButton _stop				= new JButton();
	private final JSlider _volume 			= new JSlider(SwingConstants.HORIZONTAL, 0, MAX_VOLUME, 0);

	@SuppressWarnings("unused") private WeakContract toAvoidGC;
	@SuppressWarnings("unused") private WeakContract _volumeListenerContract;

	ControlPanel() {
		super(new FlowLayout(FlowLayout.LEFT, 6, 5));

	    toAvoidGC = _controller.isPlaying().addReceiver(new Consumer<Boolean>() { @Override public void consume(Boolean isPlaying) {
	    	if (isMyOperatingMode())
	    		_pauseResume.setText(isPlaying ? PAUSE_ICON : RESUME_ICON);
	    	else
	    		_pauseResume.setText(RESUME_ICON);
		}});
	    _volumeListenerContract = _controller.volumePercent().addReceiver(new Consumer<Integer>() { @Override public void consume(Integer volume) {
				_volume.setValue(volumeLevel(volume));
			}
		});
	    
	    _pauseResume.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent evt) {
	    	pauseResumeActionPerformed();
        }});
	    add(_pauseResume);

//	    _back.setText(BACK_ICON);
//	    _back.addActionListener(new ActionListener() {
//	        public void actionPerformed(ActionEvent evt) {
//	            backActionPerformed();
//	        }
//	    });
//	    add(_back);

	    _skip.setText(SKIP_ICON);
	    _skip.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent evt) {
	    	skipActionPerformed();
	    }});
	    add(_skip);

	    _stop.setText(STOP_ICON);
//	    _stop.setForeground(Color.RED);
	    _stop.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent evt) {
	    	stopActionPerformed();
	    }});
	    add(_stop);
	    
	    _volume.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				volumeChanged(_volume.getValue());
			}
	    });
	    add(_volume);
	}

	void update(OperatingMode operatingMode) {
		if (isMyOperatingMode(operatingMode))
			enableButtons();
		else
			disableButtons();
	}

	void enableButtons() {
		_skip.setEnabled(true);
		_stop.setEnabled(true);
	}

	void disableButtons() {
		_skip.setEnabled(false);
		_stop.setEnabled(false);
	}

	private void pauseResumeActionPerformed() {
		switchOperatingModeIfNecessary();
		_controller.pauseResume();
	}

	private void switchOperatingModeIfNecessary() {
		if (isMyOperatingMode()) return;

		activateMyOperatingMode();
	}

//	private void backActionPerformed() {
//	    Wusic.back();
//	}

	private void skipActionPerformed() {
	    _controller.skip();
	}

	private void stopActionPerformed() {
	    _controller.stop();
	}
	
	private void volumeChanged(int level) {
		_controller.volumePercent(volumePercentage(level));
	}

	private static int volumePercentage(int level) {
		return 100 * level / MAX_VOLUME;
	}
	
	private static int volumeLevel(int percent) {
		return percent * MAX_VOLUME / 100;
	}
	
	private boolean isMyOperatingMode() {
		return isMyOperatingMode(_controller.operatingMode().currentValue());
	}

	abstract boolean isMyOperatingMode(OperatingMode operatingMode);

	abstract void activateMyOperatingMode();

}
