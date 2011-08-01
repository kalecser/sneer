package dfcsantos.tracks.tastematching;

import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.hardware.io.prevalence.nature.Transaction;
import sneer.bricks.network.social.Contact;
import sneer.foundation.brickness.Brick;

@Brick (Prevalent.class)
public interface TasteMatcher {

	/** opinion - null means no opinion (unknown track). The Prevalent nature still does not support enums :( (July 2011) */
	@Transaction
    float rateEndorsement(Contact sender, String folder, Boolean opinion); 

}
