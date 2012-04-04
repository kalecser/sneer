package sneer.bricks.network.social.navigation;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.software.bricks.snapploader.Snapp;
import basis.brickness.Brick;
import basis.lang.Consumer;

@Snapp
@Brick
public interface ContactNavigator {

	void searchContactsOf(Seal seal, Consumer<ContactOfContact> consumer);

}
