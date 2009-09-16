package dfcsantos.wusic.gui.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.skin.main.menu.MainMenu;
import sneer.foundation.environments.Environments;
import dfcsantos.wusic.Wusic;
import dfcsantos.wusic.Wusic.SongSource;
import dfcsantos.wusic.gui.WusicGui;

/**
 *
 * @author daniel
 */
public class WusicGuiImpl extends javax.swing.JFrame implements WusicGui{

	private static final Wusic Wusic = my(Wusic.class);
	private boolean _isInitialized = false;
    
    {
    	
		Environments.my(MainMenu.class).addAction("Wusic", new Runnable() { @Override public void run() {
			if (!_isInitialized){
				_isInitialized = true;
				initComponents();
			}
			setVisible(true);
			Wusic.start();
		}});
	}

    private void initComponents() {

        songsSource = new javax.swing.ButtonGroup();
        mySongs = new javax.swing.JRadioButton();
        songsFromPeers = new javax.swing.JRadioButton();
        playingLabel = new javax.swing.JLabel();
        pauseButton = new javax.swing.JButton();
        skipButton = new javax.swing.JButton();
        meTooButton = new javax.swing.JButton();
        noWayButton = new javax.swing.JButton();
        mainMenuBar = new javax.swing.JMenuBar();
        mainMenu = new javax.swing.JMenu();
        chooseMySongsFolder = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Wusic");

        songsSource.add(mySongs);
        mySongs.setSelected(true);
        mySongs.setText("Play My Songs");
        mySongs.setName("mySongs"); // NOI18N
        mySongs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mySongsActionPerformed();
            }
        });

        songsSource.add(songsFromPeers);
        songsFromPeers.setText("Play Songs From Peers");
        songsFromPeers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                songsFromPeersActionPerformed();
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

        mainMenu.setText("File");

        chooseMySongsFolder.setText("Choose Song Folder");
        chooseMySongsFolder.setName("chooseSongFolderMenu"); // NOI18N
        chooseMySongsFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseMySongsFolderActionPerformed();
            }
        });
        mainMenu.add(chooseMySongsFolder);
        chooseMySongsFolder.getAccessibleContext().setAccessibleName("chooseSongFolderMenu");

        mainMenuBar.add(mainMenu);

        setJMenuBar(mainMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(playingLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                    .addComponent(mySongs, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(songsFromPeers, javax.swing.GroupLayout.Alignment.LEADING)
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
                .addComponent(mySongs)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(songsFromPeers)
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

        pack();
    }// </editor-fold>

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

    private void mySongsActionPerformed() {
        Wusic.chooseSongSource(SongSource.MY_SONGS);
    }

    private void songsFromPeersActionPerformed() {
        Wusic.chooseSongSource(SongSource.PEER_SONGS_STAGING_AREA);
    }

    private void chooseMySongsFolderActionPerformed() {
        // TODO add your handling code here:
    }

    private javax.swing.JMenuItem chooseMySongsFolder;
    private javax.swing.JMenu mainMenu;
    private javax.swing.JMenuBar mainMenuBar;
    private javax.swing.JButton meTooButton;
    private javax.swing.JRadioButton mySongs;
    private javax.swing.JButton noWayButton;
    private javax.swing.JButton pauseButton;
    private javax.swing.JLabel playingLabel;
    private javax.swing.JButton skipButton;
    private javax.swing.JRadioButton songsFromPeers;
    private javax.swing.ButtonGroup songsSource;

}

