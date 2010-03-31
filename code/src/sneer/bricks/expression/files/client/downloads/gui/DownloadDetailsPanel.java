package sneer.bricks.expression.files.client.downloads.gui;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.foundation.lang.Consumer;

class DownloadDetailsPanel extends JPanel {

	private final Container _parent;

	private final JLabel _downloadLabel;

	private final JProgressBar _downloadProgressBar;

	@SuppressWarnings("unused") private WeakContract _toAvoidGC;
	@SuppressWarnings("unused") private final WeakContract _toAvoidGC2;

	DownloadDetailsPanel(Container parent, Download download) {
		_parent = parent;

		_downloadLabel = newLabelFor(download);
		add(_downloadLabel);

		_downloadProgressBar = newProgressBarFor(download);
		add(_downloadProgressBar);

		setPreferredSize(new Dimension(500, 100));

		_toAvoidGC2 = download.finished().addPulseReceiver(new Runnable() { @Override public void run() {
			close();
		}});
	}

	private JProgressBar newProgressBarFor(Download download) {
		JProgressBar progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);

		_toAvoidGC = download.progress().addReceiver(new Consumer<Integer>() { @Override public void consume(Integer progress) {
			_downloadProgressBar.setValue(progress);
		}});

		return progressBar;
	}

	private JLabel newLabelFor(Download download) {
		return new JLabel(download.toString());
	}

	private void close() {
		_parent.remove(this);
	}

}
