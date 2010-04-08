package dfcsantos.tracks.tastematching;

import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.foundation.brickness.Brick;

@Brick (Prevalent.class)
public interface MusicalTasteMatcher {

    void processEndorsement(String senderNickname, String folder, boolean isKnownTrack);

    float ratingFor(String senderNickname, String folder);

}
