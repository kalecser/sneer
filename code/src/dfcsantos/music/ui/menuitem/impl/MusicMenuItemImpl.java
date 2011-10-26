package dfcsantos.music.ui.menuitem.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.skin.main.menu.MainMenu;
import sneer.foundation.lang.Closure;
import dfcsantos.music.ui.menuitem.MusicMenuItem;
import dfcsantos.music.ui.presenter.MusicPresenter;

class MusicMenuItemImpl implements MusicMenuItem {
	{
		my(MainMenu.class).menu().addAction(40, "Music", new Closure() { @Override public void run() {
			my(MusicPresenter.class);
		}});
	}
}
