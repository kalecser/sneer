package sneer.bricks.expression.files.client.downloads.gui;

import static sneer.foundation.environments.Environments.my;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.bricks.skin.widgets.reactive.Widget;

class DownloadDetailsPanel extends JPanel {

	private final DownloadsPanel _parent;

	private final JLabel _sourceAndfile;
	private final Widget<JProgressBar> _progress;

	@SuppressWarnings("unused") private final WeakContract _toAvoidGC;

	DownloadDetailsPanel(DownloadsPanel parent, Download download) {
		super(new GridLayout(2, 1, 0, 1));

		_parent = parent;

		String source = my(ContactSeals.class).contactGiven(download.source()).nickname().currentValue();
		String file = my(Lang.class).strings().abbreviate(download.file().getName(), 50);
		_sourceAndfile = new JLabel(source + " :: " + file);
		add(_sourceAndfile);

		_progress = my(ReactiveWidgetFactory.class).newProgressBar(download.progress());
		add(_progress.getMainWidget());

		_toAvoidGC = download.finished().addPulseReceiver(new Runnable() { @Override public void run() {
			close();
		}});
	}

	private void close() {
		_parent.remove(this);
		_parent.update();
	}

}
