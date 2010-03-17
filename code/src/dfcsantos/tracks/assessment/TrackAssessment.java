package dfcsantos.tracks.assessment;

import sneer.bricks.network.social.Contact;
import dfcsantos.tracks.Track;

public interface TrackAssessment {

	Track track();

	Contact trackSource();

	int score();

}
