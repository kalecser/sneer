package dfcsantos.tracks.tastematching;

import sneer.bricks.network.social.Contact;
import sneer.foundation.brickness.Brick;

@Brick
public interface MusicalTasteMatcher {

    void processEndorsementOfKnownTrack(Contact sender, String folder);

    float processEndorsementOfUnknownTrackAndReturnMatchRating(Contact sender, String folder);

}
