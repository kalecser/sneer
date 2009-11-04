package dfcsantos.tracks.assessment;

import sneer.foundation.brickness.Brick;
import dfcsantos.tracks.Track;

@Brick
public interface TrackAssessor {

	TrackAssessment approve(Track track);

	TrackAssessment reject(Track track);

}
