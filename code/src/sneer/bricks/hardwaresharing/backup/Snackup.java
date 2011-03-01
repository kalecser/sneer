package sneer.bricks.hardwaresharing.backup;

import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.exceptions.Refusal;

@Brick
public interface Snackup {

	Consumer<String> folderToSyncSetter();
	void lendSpaceTo(Contact contact, int megaBytes) throws Refusal;
	Signal<Boolean> isSynced();

}
