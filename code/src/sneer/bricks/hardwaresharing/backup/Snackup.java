package sneer.bricks.hardwaresharing.backup;

import basis.brickness.Brick;
import basis.lang.Consumer;
import basis.lang.exceptions.Refusal;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;

@Brick
public interface Snackup {

	Signal<String> folderToSync();
	Consumer<String> folderToSyncSetter();
	void lendSpaceTo(Contact contact, int megaBytes) throws Refusal;
	Signal<Boolean> isSynced();

}
