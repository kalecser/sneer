package sneer.bricks.skin.widgets.reactive;

import java.awt.Image;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListModel;

import sneer.bricks.hardware.gui.nature.GUI;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.pulp.reactive.signalchooser.SignalChooser;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.PickyConsumer;

@Brick(GUI.class)
public interface ReactiveWidgetFactory {

	ImageWidget newImage(Signal<Image> source);
	Widget<JFrame> newFrame(Signal<?> title);
	
	TextWidget<JLabel> newLabel(Signal<?> source);
	TextWidget<JLabel> newLabel(Signal<String> source, String synthName);

	ToggleButtonWidget<JCheckBox> newCheckBox(Signal<Boolean> source, Consumer<Boolean> setter);
	ToggleButtonWidget<JCheckBox> newCheckBox(Signal<Boolean> source, Consumer<Boolean> setter, Runnable cascadeRefreshOperations);

	Widget<JProgressBar> newProgressBar(Signal<Integer> source);

	TextWidget<JTextField> newEditableLabel(Signal<?> source, PickyConsumer<? super String> setter);
	TextWidget<JTextField> newEditableLabel(Signal<?> source, PickyConsumer<? super String> setter, NotificationPolicy notificationPolicy);
	
	TextWidget<JTextField> newTextField(Signal<?> source, PickyConsumer<? super String> setter);
	TextWidget<JTextField> newTextField(Signal<?> source, PickyConsumer<? super String> setter, NotificationPolicy notificationPolicy);
	
	TextWidget<JTextPane> newTextPane(Signal<?> source, PickyConsumer<? super String> setter);
	TextWidget<JTextPane> newTextPane(Signal<?> source, PickyConsumer<? super String> setter, NotificationPolicy notificationPolicy);

	<T> ListWidget<T> newList(ListSignal<T> source);
	<T> ListWidget<T> newList(ListSignal<T> source, LabelProvider<T> labelProvider);
	
	<T> ListModel newListSignalModel(ListSignal<T> input, SignalChooser<T> chooser);
}