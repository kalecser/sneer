package sneer.bricks.snapps.dns;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.foundation.brickness.Brick;

@Brick
public interface Dns {

	ListSignal<String> knownIpsForContact(Seal sealForContact);

}
