package spikes.klaus.wanderer.sneer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sneer.tests.SovereignParty;
import sneer.tests.adapters.SneerCommunity;

public class SneerCommunityWanderer {

	private final Chooser _chooser;
	private final SneerCommunity _community;
	private final List<PartyWanderer> _parties = new ArrayList<PartyWanderer>();
	private final NameGenerator _nameGenerator;


	public SneerCommunityWanderer(Chooser chooser, File tmpFolder) {
		_chooser = chooser;
		_community = new SneerCommunity(tmpFolder);
		_nameGenerator = new NameGenerator(_chooser);
	}


	public void wander() {
		if (_parties.size() < 5) {
			_parties.add(connectNewParty());
			return;
		}

		_chooser.pickOne(_parties).wander();
	}


	private PartyWanderer connectNewParty() {
		SovereignParty result = _community.createParty(_nameGenerator.generateName());
		for (PartyWanderer other : _parties) {
			if (_chooser.nextBoolean())
				_community.connect(result, other.delegate());
		}
		return new PartyWanderer(result);
	}

}
