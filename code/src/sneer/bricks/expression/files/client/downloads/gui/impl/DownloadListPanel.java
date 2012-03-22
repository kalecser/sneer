package sneer.bricks.expression.files.client.downloads.gui.impl;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import basis.lang.Consumer;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.reactive.collections.SetSignal;

class DownloadListPanel extends Box {

	@SuppressWarnings("unused") private WeakContract _toAvoidGC;
	private final SetSignal<Download> _downloads;

	
	public DownloadListPanel(final SetSignal<Download> downloads) {
		super(BoxLayout.Y_AXIS);
		_downloads = downloads;

		setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

		_toAvoidGC = downloads.addReceiver(new Consumer<Object>() { @Override public void consume(Object ignored) {
			refresh();
		}});
	}

	
	private void refresh() {
		removeAll();
		for (Download download : _downloads) 
			add(newDownloadPanelFor(download));
		smartPack();
	}


	private void smartPack() {
		Container ancestor = getTopLevelAncestor();
		if (ancestor == null) return; //Don't you just love AWT?
		((Window) ancestor).pack(); // Fix: Is there another way?
	}

	
	private JPanel newDownloadPanelFor(Download download) {
		JPanel result = new DownloadPanel(download);
		result.setMaximumSize(new Dimension(340, 60));
		result.setBorder(BorderFactory.createEmptyBorder(0, 6, 6, 6));
		result.setAlignmentX(CENTER_ALIGNMENT);
		result.setAlignmentY(CENTER_ALIGNMENT);
		return result;
	}

}
