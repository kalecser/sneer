package sneer.bricks.skin.menu.impl;

import javax.swing.JMenu;

class MenuGroupImpl extends AbstractMenuGroup<JMenu> {

	protected final JMenu _menu;
	
	MenuGroupImpl(String text) {
		_menu = new JMenu();
		_menu.setText(text);
	}

	@Override
	public JMenu getWidget() {
		return _menu;
	}
}