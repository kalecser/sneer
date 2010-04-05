package sneer.bricks.expression.files.client.downloads.gui;

import static sneer.foundation.environments.Environments.my;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.foundation.lang.Consumer;

class DownloadDetailsPanel extends JPanel {

	private final Container _parent;

	private final JLabel _downloadFile;

	private final JLabel _downloadSource;

	private final JProgressBar _downloadProgress;

	@SuppressWarnings("unused") private WeakContract _toAvoidGC;
	@SuppressWarnings("unused") private final WeakContract _toAvoidGC2;

	DownloadDetailsPanel(Container parent, Download download) {
		super(new GridLayout(3, 1, 0, 1));

		_parent = parent;

		_downloadFile = new JLabel("File: " + my(Lang.class).strings().abbreviate(download.file().getName(), 100));
		add(_downloadFile);

		_downloadSource = new JLabel("From: " + download.source().nickname().currentValue());
		add(_downloadSource);

		_downloadProgress = newProgressBarFor(download);
		add(_downloadProgress);

		setPreferredSize(new Dimension(500, 100));

		_toAvoidGC2 = download.finished().addPulseReceiver(new Runnable() { @Override public void run() {
			close();
		}});
	}

	private JProgressBar newProgressBarFor(Download download) {
		JProgressBar progressBar = new JProgressBar(0, 100);
		progressBar.setSize(450, 16);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);

		_toAvoidGC = download.progress().addReceiver(new Consumer<Float>() { @Override public void consume(Float progress) {
			System.out.println("Download progress status: " + progress + "%");
			_downloadProgress.setValue(progress.intValue());
		}});

		return progressBar;
	}

	private void close() {
		_parent.remove(this);
	}

}
