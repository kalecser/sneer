package dfcsantos.tracks.assessment.assessor;

import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.assessment.TrackAssessment;

@Brick
public interface TrackAssessor {

	TrackAssessment approve(Track track);

	TrackAssessment reject(Track track);

	Signal<TrackAssessment> lastAssessment();

}
