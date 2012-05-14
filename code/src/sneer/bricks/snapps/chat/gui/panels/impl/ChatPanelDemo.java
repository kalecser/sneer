package sneer.bricks.snapps.chat.gui.panels.impl;

import static basis.environments.Environments.my;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import sneer.bricks.hardware.gui.guithread.GuiThread;
import sneer.bricks.hardware.gui.timebox.TimeboxedEventQueue;
import sneer.bricks.snapps.system.log.sysout.LogToSysout;
import basis.brickness.Brickness;
import basis.environments.Environments;
import basis.lang.Closure;

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
		
//		final ListRegister<ChatMessage> messages = my(CollectionSignals.class).newListRegister();
//		Consumer<String> messageSender = new Consumer<String>() {
//
//			@Override
//			public void consume(String value) {
//				messages.add(new ChatMessage(value));
//			}
//			
//		};
		//final JPanel instrumentPanel = my(ChatPanels.class).newPanel(messages.output(), messageSender);
		jFrame.setLayout(new BorderLayout());
		//jFrame.add(instrumentPanel, BorderLayout.CENTER);
		jFrame.setBounds(100, 100, 200, 300);
		jFrame.pack();
		jFrame.setVisible(true);
	}
}
