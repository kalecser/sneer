package sneer.bricks.skin.widgets.reactive;

import java.awt.Image;

import javax.swing.JPanel;

import basis.lang.PickyConsumer;

import sneer.bricks.pulp.reactive.Signal;

public interface ImageWidget extends ComponentWidget<JPanel>{

	Signal<Image> output();
	PickyConsumer<Image> setter();	
}