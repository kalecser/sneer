package sneer.bricks.skin.menu.impl;

import javax.swing.JPopupMenu;

class MenuPopupImpl extends AbstractMenuGroup<JPopupMenu> {

	protected final JPopupMenu _menu;

	protected MenuPopupImpl() {
		_menu = new JPopupMenu();
	}

	@Override
	public JPopupMenu getWidget() {
		return _menu;
	}
}
