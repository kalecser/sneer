package sneer.bricks.expression.files.client.downloads.gui;

import static sneer.foundation.environments.Environments.my;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.foundation.lang.Consumer;

class DownloadDetailsPanel extends JPanel {

	private final DownloadsPanel _parent;

	private final JLabel _sourceAndfile;
	private final JProgressBar _progress;

	@SuppressWarnings("unused") private final WeakContract _toAvoidGC;
	@SuppressWarnings("unused") private final WeakContract _toAvoidGC2;

	DownloadDetailsPanel(DownloadsPanel parent, Download download) {
		super(new GridLayout(2, 1, 0, 1));

		_parent = parent;

		String source = download.source().nickname().currentValue();
		String file = my(Lang.class).strings().abbreviate(download.file().getName(), 50);
		_sourceAndfile = new JLabel(source + " :: " + file);
		add(_sourceAndfile);

		_progress = new JProgressBar(0, 100);
		_toAvoidGC = download.progress().addReceiver(new Consumer<Integer>() { @Override public void consume(Integer progress) {
			_progress.setValue(progress);
		}});
		_progress.setStringPainted(true);
		add(_progress);

		_toAvoidGC2 = download.finished().addPulseReceiver(new Runnable() { @Override public void run() {
			close();
		}});
	}

	private void close() {
		_parent.remove(this);
		_parent.update();
	}

}
