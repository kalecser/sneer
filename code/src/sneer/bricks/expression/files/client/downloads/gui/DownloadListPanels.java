package sneer.bricks.expression.files.client.downloads.gui;

import java.awt.Component;

import basis.brickness.Brick;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.pulp.reactive.collections.SetSignal;

@Brick //(GUI.class) //Fix: Adding the gui nature here would be the right thing to do but it causes deadlock.
public interface DownloadListPanels {

	Component produce(SetSignal<Download> downloads);

}
