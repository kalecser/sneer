package spikes.klaus.wanderer.sneer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sneer.tests.SovereignParty;
import sneer.tests.adapters.SneerCommunity;

public class SneerCommunityWanderer {

	private final SneerCommunity _community;
	private final List<SovereignParty> _parties = new ArrayList<SovereignParty>();


	public SneerCommunityWanderer(File tmpFolder) {
		_community = new SneerCommunity(tmpFolder);
	}


	public void wanderAt(Random random) {
		if (_parties.isEmpty()) {
			createParty(random);
			return;
		}
		
		if (random.nextInt(10) == 0) {
			
			return;
		}
		
	}
	
	
	private void createParty(Random random) {
		String name = new NameGenerator().generateName(random);
		SovereignParty party = _community.createParty(name);
		_parties.add(party);
	}


}
