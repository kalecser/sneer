package spikes.demos;

import static sneer.foundation.environments.Environments.my;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTextField;

import sneer.bricks.hardware.gui.guithread.GuiThread;
import sneer.bricks.hardware.gui.timebox.TimeboxedEventQueue;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.widgets.reactive.NotificationPolicy;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.bricks.skin.widgets.reactive.TextWidget;
import sneer.bricks.skin.widgets.reactive.ToggleButtonWidget;
import sneer.foundation.brickness.Brickness;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Consumer;

public class ReactiveToggleButtonDemo {

	private ReactiveToggleButtonDemo() {
		my(TimeboxedEventQueue.class).startQueueing(500000);

		my(GuiThread.class).invokeAndWait(new Runnable() { @Override public void run() {
			final ReactiveWidgetFactory rfactory = my(ReactiveWidgetFactory.class);
			JFrame frame = new JFrame("Reactive Check Box Demo");

			final Register<Boolean> isChecked = my(Signals.class).newRegister(false);
			ToggleButtonWidget<JCheckBox> toggleButtonWidget = rfactory.newCheckBox(isChecked.output(), isChecked.setter());
			JCheckBox rCheckBox = toggleButtonWidget.getMainWidget();
			rCheckBox.setText("Reactive CheckBox");
			frame.getContentPane().add(toggleButtonWidget.getComponent());

			TextWidget<JTextField> textWidget = rfactory.newTextField(isChecked.output(), new Consumer<String>() { @Override public void consume(String booleanString) {
				isChecked.setter().consume(Boolean.parseBoolean(booleanString));
			}}, NotificationPolicy.OnEnterPressedOrLostFocus);
			JTextField rTextField = textWidget.getMainWidget();
			rTextField.setPreferredSize(new Dimension(42, 14));
			frame.getContentPane().add(textWidget.getComponent());

			frame.setLayout(new FlowLayout());
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			frame.pack();
		}});
	}

	public static void main(String[] args) {
		Environments.runWith(Brickness.newBrickContainer(), new Runnable(){ @Override public void run() {
			try {
				new ReactiveToggleButtonDemo();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}});
	}

}
