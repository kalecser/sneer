package sneer.bricks.hardwaresharing.backup;

import sneer.bricks.network.social.Contact;
import sneer.foundation.brickness.Brick;


@Brick
public interface Snackup {

	void sync();

	void lendBackupSpaceTo(Contact contact, int megaBytes);

}
