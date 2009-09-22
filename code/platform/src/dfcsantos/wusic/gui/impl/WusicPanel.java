package dfcsantos.wusic.gui.impl;

import static sneer.foundation.environments.Environments.my;
import dfcsantos.wusic.Wusic;
import dfcsantos.wusic.Wusic.TrackSource;

/**
 * 
 * @author daniel
 */
class WusicPanel extends javax.swing.JPanel {

	private static final Wusic Wusic = my(Wusic.class);


    private javax.swing.JButton meTooButton;
    private javax.swing.JRadioButton myTracks;
    private javax.swing.JButton noWayButton;
    private javax.swing.JButton pauseButton;
    private javax.swing.JLabel playingLabel;
    private javax.swing.JButton skipButton;
    private javax.swing.JRadioButton tracksFromPeers;
    private javax.swing.ButtonGroup tracksSource;

    

	{
//		playingLabel = my(ReactiveWidgetFactory.class).newLabel(Wusic.trackPlaying()).getMainWidget();
        tracksSource = new javax.swing.ButtonGroup();
        myTracks = new javax.swing.JRadioButton();
        tracksFromPeers = new javax.swing.JRadioButton();
        playingLabel = new javax.swing.JLabel();
        pauseButton = new javax.swing.JButton();
        skipButton = new javax.swing.JButton();
        meTooButton = new javax.swing.JButton();
        noWayButton = new javax.swing.JButton();

        tracksSource.add(myTracks);
        myTracks.setSelected(true);
        myTracks.setText("Play My Songs");
        myTracks.setName("mySongs"); // NOI18N
        myTracks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myTracksActionPerformed();
            }
        });

        tracksSource.add(tracksFromPeers);
        tracksFromPeers.setText("Play Songs From Peers");
        tracksFromPeers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tracksFromPeersActionPerformed();
            }
        });

        playingLabel.setFont(new java.awt.Font("Tahoma", 2, 14));
        playingLabel.setText("Playing Label - Playing Label 00:00");

        pauseButton.setText("> / ||");
        pauseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseButtonActionPerformed();
            }
        });

        skipButton.setText(">>");
        skipButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                skipButtonActionPerformed();
            }
        });

        meTooButton.setText("Me Too :)");
        meTooButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meTooButtonActionPerformed();
            }
        });

        noWayButton.setText("No Way :(");
        noWayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noWayButtonActionPerformed();
            }
        });
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(playingLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                    .addComponent(myTracks, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tracksFromPeers, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(pauseButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(skipButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(meTooButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(noWayButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(myTracks)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tracksFromPeers)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(playingLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pauseButton)
                    .addComponent(skipButton)
                    .addComponent(meTooButton)
                    .addComponent(noWayButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
	}
    private void pauseButtonActionPerformed() {                                            
    	Wusic.pauseResume();
    }                                           

    private void skipButtonActionPerformed() {
        Wusic.skip();
    }

    private void meTooButtonActionPerformed() {
        Wusic.meToo();
    }

    private void noWayButtonActionPerformed() {
        Wusic.noWay();
    }

    private void myTracksActionPerformed() {
        Wusic.chooseTrackSource(TrackSource.MY_TRACKS);
    }

    private void tracksFromPeersActionPerformed() {
        Wusic.chooseTrackSource(TrackSource.PEER_TRACKS_STAGING_AREA);
    }
}
