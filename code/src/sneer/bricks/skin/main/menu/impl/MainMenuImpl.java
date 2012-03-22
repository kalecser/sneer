package sneer.bricks.skin.main.menu.impl;

import static basis.environments.Environments.my;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.skin.menu.MenuFactory;
import sneer.bricks.skin.menu.MenuGroup;

class MainMenuImpl implements MainMenu {

	private final MenuGroup<JMenuBar> _menuBar = my(MenuFactory.class).createMenuBar();
	private MenuGroup<JMenu> _delegate;
	
	@Override public JMenuBar getMenuBarWidget() {
		return _menuBar.getWidget();
	}

	
	@Override
	synchronized
	public MenuGroup<JMenu> menu() {
		if (_delegate == null) initMenu();
		return _delegate;
	}

	
	private void initMenu() {
		_delegate = my(MenuFactory.class).createMenuGroup("Menu");
		_delegate.getWidget().setName("MainMenu");
		_menuBar.addGroup(0, _delegate);
	}

}