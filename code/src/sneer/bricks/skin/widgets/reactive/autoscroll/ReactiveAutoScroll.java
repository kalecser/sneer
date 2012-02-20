package sneer.bricks.skin.widgets.reactive.autoscroll;

import javax.swing.JScrollPane;

import sneer.bricks.hardware.gui.nature.GUI;
import sneer.bricks.pulp.notifiers.Source;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Consumer;

@Brick(GUI.class)
public interface ReactiveAutoScroll {
	
	<T> JScrollPane create(Source<T> eventSource, Consumer<T> receiver);

}