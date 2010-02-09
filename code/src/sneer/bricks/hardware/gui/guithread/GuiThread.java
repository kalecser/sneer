package sneer.bricks.hardware.gui.guithread;

import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Closure;

@Brick
public interface GuiThread {

	void invokeAndWait(Closure closure);
	void invokeAndWaitForWussies(Closure closure);
	void invokeLater(Closure closure);

	void assertInGuiThread();
	void assertNotInGuiThread();
	
}
