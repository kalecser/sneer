package dfcsantos.tracks.tastematching;

import sneer.bricks.network.social.Contact;
import sneer.foundation.brickness.Brick;

@Brick
public interface MusicalTasteMatcher {

    void processEndorsement(Contact sender, String folder, boolean isKnownTrack);

    float ratingFor(Contact sender, String folder);

}
