package sneer.bricks.skin.widgets.reactive.autoscroll;

import javax.swing.JScrollPane;

import basis.brickness.Brick;
import basis.lang.Consumer;

import sneer.bricks.hardware.gui.nature.GUI;
import sneer.bricks.pulp.notifiers.Source;

@Brick(GUI.class)
public interface ReactiveAutoScroll {
	
	<T> JScrollPane create(Source<T> eventSource, Consumer<T> receiver);

}