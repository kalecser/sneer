package sneer.bricks.hardware.io.prevalence.flag;

import sneer.foundation.brickness.Brick;

/** Like all regular bricks, this brick is loaded OUTSIDE the prevalent environment so it always returns false when asked isInsidePrevalence(). The prevalent environment provides another binding for this interface that returns true. See implementing classes. */   
@Brick
public interface PrevalenceFlag {

	boolean isInsidePrevalence();

}
