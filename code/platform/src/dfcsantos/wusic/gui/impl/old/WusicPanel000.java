package dfcsantos.wusic.gui.impl.old;

import static sneer.foundation.environments.Environments.my;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import sneer.bricks.skin.notmodal.filechooser.FileChoosers;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.foundation.lang.Consumer;
import dfcsantos.wusic.Wusic;

/**
 * 
 * @author daniel
 */
class WusicPanel000 extends javax.swing.JPanel {

	private static final Wusic Wusic = my(Wusic.class);

	private final JButton selectFolderButton;
	private final JButton skipButton;
	private final JButton pauseButton;
	private final JButton playButton;
	private final JLabel playingLabel;
	private final JFileChooser _selectFolderFileChooser;

	{
		playingLabel = my(ReactiveWidgetFactory.class).newLabel(Wusic.songPlaying()).getMainWidget();
		playButton = new javax.swing.JButton();
		skipButton = new javax.swing.JButton();
		pauseButton = new javax.swing.JButton();
		selectFolderButton = new javax.swing.JButton();

		skipButton.setText(">>");
		skipButton.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent ignored) {
			Wusic.skip();
		}});

		pauseButton.setText("||");
		pauseButton.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent ignored) {
			Wusic.pauseResume();
		}});

		playButton.setText(">");
		playButton.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent ignored) {
			Wusic.start();
		}});
		
		selectFolderButton.setText("Select Folder");
		selectFolderButton.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent arg0) {
				_selectFolderFileChooser.showDialog(null, "Select the mp3 files folder");
		}});
		_selectFolderFileChooser = my(FileChoosers.class).newFileChooser(new Consumer<File>() { @Override public void consume(File selectedFolder) {
			if (selectedFolder == null) return;
			Wusic.setMySongsFolder(selectedFolder);
			Wusic.skip();
		}});
		_selectFolderFileChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createParallelGroup(
												javax.swing.GroupLayout.Alignment.TRAILING,
												false)
										.addGroup(
												layout
														.createSequentialGroup()
														.addComponent(
																playingLabel,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(
																selectFolderButton))
										.addGroup(
												javax.swing.GroupLayout.Alignment.LEADING,
												layout
														.createSequentialGroup()
														.addComponent(
																playButton)
														.addGap(2, 2, 2)
														.addComponent(
																skipButton)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(
																pauseButton)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														)));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																playingLabel,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																19,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																selectFolderButton))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																playButton)
														.addComponent(
																skipButton)
														.addComponent(
																pauseButton)
														)));
	}

}
