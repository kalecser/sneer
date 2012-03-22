package dfcsantos.music.ui.presenter.menuitem.impl;

import static basis.environments.Environments.my;
import basis.lang.Closure;
import sneer.bricks.skin.main.menu.MainMenu;
import dfcsantos.music.ui.presenter.MusicPresenter;
import dfcsantos.music.ui.presenter.menuitem.MusicMenuItem;

class MusicMenuItemImpl implements MusicMenuItem {
	{
		my(MainMenu.class).menu().addAction(40, "Music", new Closure() { @Override public void run() {
			my(MusicPresenter.class);
		}});
	}
}
