package sneer.bricks.network.social.navigation;

import sneer.bricks.identity.seals.Seal;
import basis.brickness.Brick;
import basis.lang.Consumer;

@Brick
public interface ContactNavigator {

	void searchContactsOf(Seal seal, Consumer<ContactOfContact> consumer);

}
