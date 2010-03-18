package dfcsantos.tracks.assessment.tastematcher;

import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick
public interface MusicalTasteMatcher {

	Signal<Contact> bestMatch();

}
