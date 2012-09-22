package sneer.bricks.snapps.web.impl;

import sneer.bricks.identity.seals.Seal;

public interface ContactProvider {

	Seal getSealForNickOrNull(String nickName);

}
