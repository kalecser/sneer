package sneer.bricks.skin.main.synth.menu.impl;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import sneer.bricks.skin.main.synth.menu.SynthMenus;

class SynthMenusImpl implements SynthMenus {
	
	@Override public JMenuBar createMenuBar(){ return new JMenuBar(); }
	@Override public JMenu createMenuGroup() { return new JMenu(); }
	@Override public JMenuItem createMenuItem() { return new JMenuItem(); }
	@Override public JCheckBoxMenuItem createCheckboxMenuItem() { return new JCheckBoxMenuItem(); }
	@Override public JPopupMenu createMenuPopup() { return new JPopupMenu(); }
}