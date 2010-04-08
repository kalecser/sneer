package dfcsantos.tracks.tastematching;

import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.foundation.brickness.Brick;

@Brick (Prevalent.class)
public interface MusicalTasteMatcher {

    void processEndorsement(String nickname, String folder, boolean isKnownTrack);

    float ratingFor(String nickname, String folder);

}
