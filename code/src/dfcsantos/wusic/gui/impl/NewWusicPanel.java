package dfcsantos.wusic.gui.impl;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class NewWusicPanel extends JPanel {

	private JTabbedPane jTabbedPane1							= new JTabbedPane();
    private PlayOwnTracksPanel playOwnTracksPanel				= new PlayOwnTracksPanel();
    private PlayTracksFromPeersPanel playTracksFromPeersPanel	= new PlayTracksFromPeersPanel();
	{
		jTabbedPane1.addTab("Play Own Tracks", playOwnTracksPanel);
		jTabbedPane1.addTab("Play Tracks From Peers", playTracksFromPeersPanel);
		
		initLayout();
	}
	 private void initLayout() {
 	    GroupLayout playOwnTracksPanelLayout = new GroupLayout(playOwnTracksPanel);
        playOwnTracksPanel.setLayout(playOwnTracksPanelLayout);
        playOwnTracksPanelLayout.setHorizontalGroup(
            playOwnTracksPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 303, Short.MAX_VALUE)
        );
        playOwnTracksPanelLayout.setVerticalGroup(
            playOwnTracksPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 122, Short.MAX_VALUE)
        );
        GroupLayout playTracksFromPeersPanelLayout = new GroupLayout(playTracksFromPeersPanel);
        playTracksFromPeersPanel.setLayout(playTracksFromPeersPanelLayout);
        playTracksFromPeersPanelLayout.setHorizontalGroup(
            playTracksFromPeersPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 303, Short.MAX_VALUE)
        );
        playTracksFromPeersPanelLayout.setVerticalGroup(
            playTracksFromPeersPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 122, Short.MAX_VALUE)
        );
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, GroupLayout.PREFERRED_SIZE, 307, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(436, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(342, Short.MAX_VALUE))
        );
    }
	 
	 public static void main(String args[]){
		JFrame frame = new JFrame();
		JPanel panel = new NewWusicPanel();
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
	 }
}
