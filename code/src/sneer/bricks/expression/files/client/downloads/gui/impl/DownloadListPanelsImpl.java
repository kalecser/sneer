package sneer.bricks.expression.files.client.downloads.gui.impl;

import java.awt.Component;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.client.downloads.gui.DownloadListPanels;
import sneer.bricks.pulp.reactive.collections.SetSignal;

class DownloadListPanelsImpl implements DownloadListPanels {

	@Override
	public Component produce(SetSignal<Download> downloads) {
		return new DownloadListPanel(downloads);
	}

}
