package sneer.bricks.softwaresharing.gui.impl;

import sneer.bricks.softwaresharing.BrickHistory;
import sneer.bricks.softwaresharing.BrickVersion;

abstract class Util {
	
	static boolean isBrickStagedForExecution(BrickHistory brickInfo) {
		for (BrickVersion version : brickInfo.versions()) 
			if(version.isChosenForExecution())
				return true;
			
		return false;
	}
}
