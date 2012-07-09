package spikes.demos;

import static basis.environments.Environments.my;

import java.awt.Container;
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFrame;

import sneer.bricks.hardware.clock.ticker.ClockTicker;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.gui.actions.Action;
import sneer.bricks.hardware.gui.timebox.TimeboxedEventQueue;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.skin.main.dashboard.InstrumentPanel;
import sneer.bricks.skin.menu.MenuGroup;
import sneer.bricks.snapps.system.blinkinglights.gui.BlinkingLightsGui;
import sneer.bricks.snapps.system.log.sysout.LogToSysout;
import basis.brickness.Brickness;
import basis.environments.Environments;
import basis.lang.Closure;

public class BlinkingLightsDemo {

	public static void main(String[] args) throws Exception {
		Environments.runWith(Brickness.newBrickContainer(), new Closure() { @Override public void run() {
			try {
				start();
			} catch (Exception e) {
				throw new basis.lang.exceptions.NotImplementedYet(e);
			}
		}});
	}

	private static void start() throws Exception {
		my(ClockTicker.class);
		
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.pack();
		frame.setVisible(true);

		my(TimeboxedEventQueue.class).startQueueing(10000);
		
		my(BlinkingLightsGui.class).init(new InstrumentPanel() {
			
			@Override
			public Container contentPane() {
				return frame.getContentPane();
			}
			
			@Override
			public MenuGroup<? extends JComponent> actions() {
				throw new basis.lang.exceptions.NotImplementedYet(); // Implement
			}
		});

		
		BlinkingLights bl = my(BlinkingLights.class);
		
		bl.turnOn(LightType.INFO, "Info", "Info - expires in 7000ms", 7000);
		bl.turnOn(LightType.WARNING, "Warning", "Warning - expires in 10000ms", 10000);
		bl.turnOn(LightType.ERROR, "Error", "This is an Error!", new NullPointerException());
		
		createInviteFrom("Edmundo");
		createInviteFrom("Klaus");

		
		Light light = my(BlinkingLights.class).prepare(LightType.GOOD_NEWS);
		my(LogToSysout.class);
		while (true) {
			my(BlinkingLights.class).turnOffIfNecessary(light);
			my(Threads.class).sleepWithoutInterruptions(1000);
			my(BlinkingLights.class).turnOnIfNecessary(light, "Teste " + System.currentTimeMillis(), "");
			my(Threads.class).sleepWithoutInterruptions(1000);
		}
				
	}

	
	private static void createInviteFrom(String name) {
		final BlinkingLights bl = my(BlinkingLights.class);
		final Light light = bl.prepare(LightType.GOOD_NEWS);
		light.addAction(new Action() {
			@Override public String caption() {return "Accept";}
			@Override public void run() {bl.turnOffIfNecessary(light);}
		});
		light.addAction(new Action() {
			@Override public String caption() {return "Reject";}
			@Override public void run() {bl.turnOffIfNecessary(light);}
		});
		bl.turnOnIfNecessary(light, "Invite from " + name, "Click accept to connect.");
	}
}
