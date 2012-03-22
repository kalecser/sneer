package sneer.bricks.hardware.gui.guithread;

import basis.brickness.Brick;
import basis.lang.Closure;

@Brick
public interface GuiThread {

	void invokeAndWait(Closure closure);
	void invokeAndWaitForWussies(Closure closure);
	void invokeLater(Closure closure);

	void assertInGuiThread();
	void assertNotInGuiThread();
	
}
