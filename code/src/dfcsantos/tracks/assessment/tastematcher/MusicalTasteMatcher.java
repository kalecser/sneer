package dfcsantos.tracks.assessment.tastematcher;

import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick //(Prevalent.class)
public interface MusicalTasteMatcher {

	void setOnOffSwitch(Signal<Boolean> onOffSwitch);

	Signal<Contact> bestMatch();

}
