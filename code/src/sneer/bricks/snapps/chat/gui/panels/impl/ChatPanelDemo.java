package sneer.bricks.snapps.chat.gui.panels.impl;

import static basis.environments.Environments.my;
import java.awt.BorderLayout;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.gui.guithread.GuiThread;
import sneer.bricks.hardware.gui.timebox.TimeboxedEventQueue;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.snapps.chat.gui.panels.ChatPanels;
import sneer.bricks.snapps.chat.gui.panels.Message;
import sneer.bricks.snapps.system.log.sysout.LogToSysout;
import basis.brickness.Brickness;
import basis.environments.Environments;
import basis.lang.Closure;
import basis.lang.Consumer;

class ChatPanelDemo {

	public static void main(String[] args) throws Exception {
		setLookAndFeel("Nimbus");
		Environments.runWith(Brickness.newBrickContainer(), new Closure() {  @Override public void run() {
			my(TimeboxedEventQueue.class).startQueueing(10000);
			my(GuiThread.class).invokeAndWait(new Closure() {
				
				@Override
				public void run() {
					demo();
				}
			});
		}});
	}


	private static void setLookAndFeel(String laf) throws Exception {
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
			if (laf.equals(info.getName()))
	            UIManager.setLookAndFeel(info.getClassName());
	}


	private static void demo() {
		my(LogToSysout.class);
		final JFrame jFrame = new JFrame();
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final ListRegister<Message> messages = my(CollectionSignals.class).newListRegister();
		Consumer<String> messageSender = new Consumer<String>() {

			@Override
			public void consume(final String value) {
				messages.add(new Message() {
					
					private long time = my(Clock.class).time().currentValue();

					@Override
					public long time() {
						return time ;
					}
					
					@Override
					public String text() {
						return value;
					}
					
					@Override
					public Image avatar() {
						return null;
					}
					
					@Override
					public String author() {
						return "Me";
					}
				});
			}
		};
		
		final JPanel instrumentPanel = my(ChatPanels.class).newPanel(messages.output(), messageSender);
		jFrame.setLayout(new BorderLayout());
		jFrame.add(instrumentPanel, BorderLayout.CENTER);
		jFrame.setBounds(100, 100, 200, 300);
		jFrame.setVisible(true);
	}
}
