package spikes.demos;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.gui.actions.Action;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.skin.main.dashboard.Dashboard;
import sneer.bricks.snapps.system.blinkinglights.gui.BlinkingLightsGui;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class BlinkingLightsDemo extends BrickTestBase {

	@Test
	public void testBL() throws Exception {
		my(Dashboard.class);
		my(BlinkingLightsGui.class);
		BlinkingLights bl = my(BlinkingLights.class);
		
		bl.turnOn(LightType.INFO, "Info", "Info - expires in 7000ms", 7000);
		bl.turnOn(LightType.WARNING, "Warning", "Warning - expires in 10000ms", 10000);
		bl.turnOn(LightType.ERROR, "Error", "This is an Error!", new NullPointerException());
		
		createInviteFrom("Edmundo");
		createInviteFrom("Klaus");
		
		Thread.sleep(60000); // holds threads for manual UI testing
	}

	private void createInviteFrom(String name) {
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
