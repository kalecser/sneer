package sneer.tests.freedom5;

import org.junit.Test;

import sneer.tests.SovereignFunctionalTestBase;
import sneer.tests.SovereignParty;



public class Freedom5Test extends SovereignFunctionalTestBase {

	
	@Test (timeout = 13000)
	public void shoutToTheWind() {
		SovereignParty c = createParty("Cid");
		SovereignParty d = createParty("Dan");
		
		connect(b(), c  );
		connect(c  , a());
		connect(c  , d  );
		
		a().shout("A!!!");
		b().shout("B!!!");
		c  .shout("C!!!");
		d  .shout("D!!!");

		a().waitForShouts("A!!!, B!!!, C!!!, D!!!");
		b().waitForShouts("A!!!, B!!!, C!!!, D!!!");
		c  .waitForShouts("A!!!, B!!!, C!!!, D!!!");
		d  .waitForShouts("A!!!, B!!!, C!!!, D!!!");
	}
	

	@Test (timeout=8000)
	public void canHearPastShouts() {
		
		a().shout("A!!!");
		b().shout("B!!!");
		
		SovereignParty c = createParty("Cid");
		connect(c, b());

		c.waitForShouts("A!!!, B!!!");
	}

}