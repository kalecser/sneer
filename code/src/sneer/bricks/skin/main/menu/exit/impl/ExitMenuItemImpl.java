package sneer.bricks.skin.main.menu.exit.impl;

import static basis.environments.Environments.my;
import basis.lang.Closure;
import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.skin.main.menu.exit.ExitMenuItem;

class ExitMenuItemImpl implements ExitMenuItem{
	{
		my(MainMenu.class).menu().addAction(100, "Exit", new Closure() { @Override public void run() {
			System.exit(0);
		}});
	}
}
