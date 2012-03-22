package sneer.bricks.hardware.gui.nature;

import basis.brickness.Brick;
import basis.brickness.Nature;
import sneer.bricks.hardware.gui.guithread.GuiThread;
import sneer.bricks.software.bricks.interception.Interceptor;

/** Bricks with this nature can freely manipulate Swing components without
 * having to worry about being in the GUI thread (AWT event dispatch thread).
 * 
 * All methods of all classes in a brick annotated with @Brick(GUI.class) will be
 * intercepted and directed to {@link GuiThread#invokeAndWaitForWussies(Runnable)} for execution.
 */

@Brick
public interface GUI extends Nature, Interceptor {}
