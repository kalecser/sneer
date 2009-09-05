package sneer.bricks.pulp.own.name.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.io.log.tests.BrickTestWithLogger;
import sneer.bricks.pulp.own.name.OwnNameKeeper;

public class OwnNameKeeperTest extends BrickTestWithLogger {

	private final OwnNameKeeper _nameKeeper = my(OwnNameKeeper.class);
	
	@Test
	public void test() throws Exception {

		_nameKeeper.nameSetter().consume("Sandro Bihaiko");
		assertEquals("Sandro Bihaiko", _nameKeeper.name().currentValue());
		
		_nameKeeper.nameSetter().consume("Nell Daux");
		assertEquals("Nell Daux", _nameKeeper.name().currentValue());
	}
}
