package sneer.bricks.expression.files.client.downloads.gui;

import java.awt.Dimension;
import java.awt.Window;

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

		setBorder(BorderFactory.createEmptyBorder(3, 7, 7, 7));

		_toAvoidGC = downloads.addReceiver(new Consumer<CollectionChange<Download>>() { @Override public void consume(CollectionChange<Download> changes) {
			for (Download startedDownload : changes.elementsAdded()) { 
				add(newDetailsPanelFor(startedDownload));
				update();
			}
		}});
	}

	void update() {
		((Window) getTopLevelAncestor()).pack(); // Fix: Is there another way?
	}

	private JPanel newDetailsPanelFor(Download download) {
		JPanel subpanel = new DownloadDetailsPanel(this, download);
		subpanel.setMaximumSize(new Dimension(320, 70));
		subpanel.setBorder(BorderFactory.createEmptyBorder(1, 4, 3, 4));
		return subpanel;
	}

}
