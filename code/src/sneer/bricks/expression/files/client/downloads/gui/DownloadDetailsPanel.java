package sneer.bricks.expression.files.client.downloads.gui;

import static sneer.foundation.environments.Environments.my;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.foundation.lang.Consumer;

class DownloadDetailsPanel extends JPanel {

	private final DownloadsPanel _parent;

	private final JLabel _downloadFileLabel;
	private final JTextField _downloadFileField;

	private final JLabel _downloadSourceLabel;
	private final JTextField _downloadSourceField;

	private final JLabel _downloadProgressLabel;
	private final JProgressBar _downloadProgressBar;

	@SuppressWarnings("unused") private final WeakContract _toAvoidGC;
	@SuppressWarnings("unused") private final WeakContract _toAvoidGC2;

	DownloadDetailsPanel(DownloadsPanel parent, Download download) {
		super(new BorderLayout(6, 0));

		_parent = parent;

		JPanel westPanel = new JPanel(new GridLayout(3, 1));

		_downloadFileLabel = new JLabel("File:");
		setAlignmentOf(_downloadFileLabel);
		westPanel.add(_downloadFileLabel);

		_downloadSourceLabel = new JLabel("Source:");
		setAlignmentOf(_downloadSourceLabel);
		westPanel.add(_downloadSourceLabel);

		_downloadProgressLabel = new JLabel("Progress:");
		setAlignmentOf(_downloadProgressLabel);
		westPanel.add(_downloadProgressLabel);


		JPanel centerPanel = new JPanel(new GridLayout(3, 1));

		_downloadFileField = new JTextField(my(Lang.class).strings().abbreviate(download.file().getName(), 80));
		_downloadFileField.setEditable(false);
		_downloadFileField.setBorder(BorderFactory.createEmptyBorder());
		setAlignmentOf(_downloadFileField);
		centerPanel.add(_downloadFileField);

		_downloadSourceField = new JTextField(download.source().nickname().currentValue());
		_downloadSourceField.setEditable(false);
		_downloadSourceField.setBorder(BorderFactory.createEmptyBorder());
		setAlignmentOf(_downloadSourceField);
		centerPanel.add(_downloadSourceField);

		_downloadProgressBar = new JProgressBar(0, 100);
		_toAvoidGC = download.progress().addReceiver(new Consumer<Integer>() { @Override public void consume(Integer progress) {
			_downloadProgressBar.setValue(progress);
		}});
		setAlignmentOf(_downloadProgressBar);
		_downloadProgressBar.setStringPainted(true);
		centerPanel.add(_downloadProgressBar);

		add(westPanel, BorderLayout.WEST);
		add(centerPanel, BorderLayout.CENTER);

		_toAvoidGC2 = download.finished().addPulseReceiver(new Runnable() { @Override public void run() {
			close();
		}});
	}

	private void setAlignmentOf(JComponent component) {
		component.setAlignmentX(LEFT_ALIGNMENT);
		component.setAlignmentY(CENTER_ALIGNMENT);
	}

	private void close() {
		_parent.remove(this);
		_parent.update();
	}

}
