package sneer.bricks.skin.popuptrigger.impl;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import basis.lang.Consumer;

import sneer.bricks.skin.popuptrigger.PopupTrigger;

public class PopupTriggerImpl implements PopupTrigger {

	final boolean isWindows = System.getProperty("os.name", "").toLowerCase().contains("windows");

	@Override
	public void listen(Component sorce, final Consumer<MouseEvent> receiver) {

		sorce.addMouseListener(new MouseAdapter(){ 
			@Override public void mousePressed(MouseEvent e) { 
				if(isWindows) return;
				receiver.consume(e); 
			}
			@Override public void mouseReleased(MouseEvent e) { 
				if(isWindows && ! e.isPopupTrigger()) return;
				receiver.consume(e); 
			}
		});
	}
}