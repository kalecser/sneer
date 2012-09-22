package sneer.bricks.snapps.web;

import sneer.bricks.software.bricks.snapploader.Snapp;
import basis.brickness.Brick;

@Brick
@Snapp
public interface Web {
	public static int PORT = 9812;
	void crash();
}
