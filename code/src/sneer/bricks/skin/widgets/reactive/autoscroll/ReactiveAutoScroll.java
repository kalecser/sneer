package sneer.bricks.skin.widgets.reactive.autoscroll;

import javax.swing.JScrollPane;

import sneer.bricks.hardware.gui.nature.GUI;
import sneer.bricks.pulp.events.EventSource;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Consumer;

@Brick(GUI.class)
public interface ReactiveAutoScroll {
	
	<T> JScrollPane create(EventSource<T> eventSource, Consumer<T> receiver);

}