package sneer.bricks.skin.menu.impl;

import javax.swing.JMenuBar;

class MenuBarImpl extends AbstractMenuGroup<JMenuBar> {

	protected final JMenuBar _bar;

	protected MenuBarImpl() {
		_bar = new JMenuBar();
	}

	@Override
	public JMenuBar getWidget() {
		return _bar;
	}
}
