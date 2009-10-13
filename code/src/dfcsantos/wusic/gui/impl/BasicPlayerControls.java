package dfcsantos.wusic.gui.impl;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.LayoutStyle;

import sneer.foundation.lang.exceptions.NotImplementedYet;

public class BasicPlayerControls extends JComponent {
	
	//FIXME private static final Wusic Wusic = my(Wusic.class);

	private JLabel _trackLabel						= new JLabel("<playing track");//FIXMEmy(ReactiveWidgetFactory.class).newLabel(Wusic.playingTrackName()).getMainWidget();
    private JLabel _trackTime						= new JLabel("00:00");//FIXMEmy(ReactiveWidgetFactory.class).newLabel(Wusic.playingTrackTime()).getMainWidget();

    private JButton _back							= new JButton(" << ");
    private JButton _pauseResume					= new JButton(" >  || ");
    private JButton _stop							= new JButton("Stop");
    private JButton _skip							= new JButton(" >> ");
    
    {
    	_trackLabel.setFont(new Font("Tahoma", 2, 14));
        _trackTime.setFont(new Font("Tahoma", 2, 14));

    	_back.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
				backButtonActionPerformed();
			}
		});
    	_pauseResume.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent evt) {
                pauseResumeButtonActionPerformed();
            }
        });
    	_skip.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                skipButtonActionPerformed();
            }
        });
    	_stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                stopButtonActionPerformed();
            }
        });

    	initLayout();

    }


    private void backButtonActionPerformed() {
    	//Wusic.back();
    	throw new NotImplementedYet("Wusic.back()");
    }
    
    private void pauseResumeButtonActionPerformed() {
    	//FIXMEWusic.pauseResume();
    }
    
    private void skipButtonActionPerformed() {
    	//FIXMEWusic.skip();
    }
    
    private void stopButtonActionPerformed() {
    	//FIXMEWusic.stop();    
    }
    
    private void initLayout() {
    	GroupLayout layout = new GroupLayout(this);
    	setLayout(layout);
    	layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
    			.addGroup(layout.createSequentialGroup()
    					.addContainerGap()
    					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
    							.addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
    									.addComponent(_trackTime)
    									.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
    									.addComponent(_trackLabel)
    							)
    							.addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
    									.addComponent(_back)
    									.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
    									.addComponent(_pauseResume)
    									.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
    									.addComponent(_stop)
    									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
    									.addComponent(_skip)
    									.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
    							)
    					)
    					.addContainerGap()
    			)
    	);
    	layout.setVerticalGroup(
    			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
    			.addGroup(layout.createSequentialGroup()
    					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
    							.addComponent(_trackTime)
    							.addComponent(_trackLabel)
    					)
    					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
    					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
    							.addComponent(_back)
    							.addComponent(_pauseResume)
    							.addComponent(_stop)
    							.addComponent(_skip)
    					)
    					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    			)
    	);
    }
}
