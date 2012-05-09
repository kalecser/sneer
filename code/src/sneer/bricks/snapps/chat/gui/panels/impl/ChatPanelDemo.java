package sneer.bricks.snapps.chat.gui.panels.impl;

import static basis.environments.Environments.my;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.ListResourceBundle;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import basis.brickness.Brickness;
import basis.environments.Environments;
import basis.lang.Closure;
import basis.lang.Consumer;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.gui.timebox.TimeboxedEventQueue;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.skin.main.dashboard.InstrumentPanel;
import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.skin.menu.MenuGroup;
import sneer.bricks.snapps.chat.ChatMessage;
import sneer.bricks.snapps.chat.gui.panels.ChatPanels;
import dfcsantos.music.ui.view.MusicView;
import dfcsantos.music.ui.view.MusicViewListener;


class ChatPanelDemo {

	public static void main(String[] args) throws Exception {
		setLookAndFeel("Nimbus");
		Environments.runWith(Brickness.newBrickContainer(), new Closure() {  @Override public void run() {
			demo();
		}});
	}


	private static void setLookAndFeel(String laf) throws Exception {
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
			if (laf.equals(info.getName()))
	            UIManager.setLookAndFeel(info.getClassName());
	}


	private static void demo() {
		my(TimeboxedEventQueue.class).startQueueing(10000);

		final JFrame jFrame = new JFrame();
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final ListRegister<ChatMessage> messages = my(CollectionSignals.class).newListRegister();
		Consumer<String> messageSender = new Consumer<String>() {

			@Override
			public void consume(String value) {
				messages.add(new ChatMessage(value));
			}
			
		};
		final JPanel instrumentPanel = my(ChatPanels.class).newPanel(messages.output(), messageSender);
		jFrame.setLayout(new BorderLayout());
		//jFrame.add(instrumentPanel, BorderLayout.CENTER);
		
		jFrame.setBounds(100, 100, 200, 300);
	//	jFrame.pack();
		jFrame.setVisible(true);
	}
}
