package sneer.bricks.expression.files.client.downloads.gui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import sneer.foundation.lang.Consumer;

public class DownloadsPanel extends Box {

	@SuppressWarnings("unused") private WeakContract _toAvoidGC;

	public DownloadsPanel(SetSignal<Download> downloads) {
		super(BoxLayout.Y_AXIS);

		_toAvoidGC = downloads.addReceiver(new Consumer<CollectionChange<Download>>() { @Override public void consume(CollectionChange<Download> changes) {
			for (Download startedDownload : changes.elementsAdded()) {
				add(newDetailsPanelFor(startedDownload));
				repaint();
			}
		}});
	}

	private JPanel newDetailsPanelFor(Download download) {
		JPanel subpanel = new DownloadDetailsPanel(this, download);
		subpanel.setAlignmentX(CENTER_ALIGNMENT);
		subpanel.setAlignmentY(CENTER_ALIGNMENT);
		subpanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		return subpanel;
	}

}
