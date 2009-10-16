package dfcsantos.wusic.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.foundation.lang.Consumer;
import dfcsantos.wusic.Wusic;

class ControlPanel extends JPanel {

	private static final Wusic Wusic = my(Wusic.class);

	private final JButton _pauseResume	= new JButton();
	private final JButton _back			= new JButton();
	private final JButton _skip			= new JButton();
	private final JButton _stop			= new JButton();

	@SuppressWarnings("unused") private WeakContract toAvoidGC;

	ControlPanel() {
		super(new FlowLayout(FlowLayout.LEFT, 9, 3));

	    toAvoidGC = Wusic.isPlaying().addReceiver(new Consumer<Boolean>() { @Override public void consume(Boolean isPlaying) {
	    	_pauseResume.setText(isPlaying ? "||" : ">");
		}});

	    _pauseResume.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent evt) {
	            pauseResumeActionPerformed();
	        }
	    });
	    add(_pauseResume);
	
	    _back.setText("<<");
	    _back.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent evt) {
	            backActionPerformed();
	        }
	    });
	    add(_back);
	
	    _skip.setText(">>");
	    _skip.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent evt) {
	        	skipActionPerformed();
	        }
	    });
	    add(_skip);
	
	    _stop.setText("Stop");
	    _stop.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent evt) {
	            stopActionPerformed();
	        }
	    });
	    add(_stop);
	}

	private void pauseResumeActionPerformed() {
		Wusic.pauseResume();
	}

	private void backActionPerformed() {
	    Wusic.back();
	}

	private void skipActionPerformed() {
	    Wusic.skip();
	}

	private void stopActionPerformed() {
	    Wusic.stop();
	}

}
