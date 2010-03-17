package dfcsantos.tracks.assessment.tastematcher.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.internetaddresskeeper.InternetAddressKeeper;
import sneer.bricks.software.bricks.statestore.BrickStateStore;
import dfcsantos.tracks.assessment.tastematcher.MusicalTasteMatcher;

abstract class Store {

	static Collection<Object[]> restore() {
		Collection<Object[]> scores  = (Collection<Object[]>) my(BrickStateStore.class).readObjectFor(MusicalTasteMatcher.class, MusicalTasteMatcherImpl.class.getClassLoader());
		return scores != null ? scores : Collections.EMPTY_LIST;
	}

	static void save(Collection<Entry<Contact, Integer>> scoresByContact) {
		List<Object[]> scores = new ArrayList<Object[]>();
		for (Entry<Contact, Integer> scoreByContact : scoresByContact) 
			scores.add(
				new Object[] {
					scoreByContact.getKey().nickname().currentValue(),  
					scoreByContact.getValue()
				}
			);

		my(BrickStateStore.class).writeObjectFor(InternetAddressKeeper.class, scores);
	 }

}
