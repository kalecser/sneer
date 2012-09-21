package snype.whisper.skin.screenshotter;

import java.awt.image.BufferedImage;

import basis.brickness.Brick;
import basis.lang.exceptions.FriendlyException;

import sneer.bricks.hardware.cpu.exceptions.Hiccup;

@Brick
public interface Screenshotter {

	BufferedImage takeScreenshot() throws FriendlyException, Hiccup;
	
}
