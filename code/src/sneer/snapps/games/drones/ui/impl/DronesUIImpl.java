package sneer.snapps.games.drones.ui.impl;

import static basis.environments.Environments.my;
import static javax.swing.JOptionPane.showInputDialog;
import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.snapps.contacts.actions.ContactAction;
import sneer.bricks.snapps.contacts.actions.ContactActionManager;
import sneer.snapps.games.drones.matches.Match;
import sneer.snapps.games.drones.ui.DronesUI;
import sneer.snapps.games.drones.units.Attributable;
import sneer.snapps.games.drones.units.UnitAttribute;

class DronesUIImpl implements DronesUI {
	
	private JFrame jFrame;
	private WeakContract timer;

	{
		my(ContactActionManager.class).addContactAction(new ContactAction(){
			@Override public String caption() { return "Game of Drones"; }
			@Override public void run() { start(); }
			@Override public boolean isVisible() { return true; }
			@Override public boolean isEnabled() { return true; }
			@Override public int positionInMenu() { return 0; }
		});
	}

	private void start() {
		defineAttributes();
		openFrame();
		startTimer();
	}

	private void defineAttributes() {
		defineAttributes(my(Match.class).unit1());
		defineAttributes(my(Match.class).unit2());
	}

	private void startTimer() {
		timer = my(Timer.class).wakeUpNowAndEvery(100, new Runnable() { @Override public void run() {
			my(Match.class).step();
			if (my(Match.class).isOver()) 
				handleGameOver();
			else
				jFrame.repaint();
		}});
	}

	private void handleGameOver() {
		JOptionPane.showMessageDialog(jFrame, "The result of the game is: " + my(Match.class).result());
		timer.dispose();
	}

	private void openFrame() {
		jFrame = new JFrame("Game of Drones") {
			@Override
			public void paint(Graphics g) {
				g.clearRect(0,0, jFrame.getWidth(), jFrame.getHeight());
				g.drawRect(my(Match.class).unit1().x(), 200, 100, 100);
				g.drawRect(my(Match.class).unit2().x(), 200, 100, 100);
			}		
			
		};
		jFrame.setVisible(true);
		jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		jFrame.setResizable(true);
		jFrame.setBounds(0, 0,800,600);
	}

	private void defineAttributes(Attributable thing) {
		for (UnitAttribute attribute : thing.attributes())
			define(attribute, thing);
	}

	private void define(UnitAttribute attribute, Attributable thing) {
		while (true)
			try {
				tryToDefine(attribute, thing);
				break;
			} catch (NumberFormatException e) {
				showMessageDialog(null, "Error. Try Again");
			}
	}

	private void tryToDefine(UnitAttribute attribute, Attributable thing) throws NumberFormatException {
		String value = showInputDialog("Value for " + thing + " " + attribute + ":"); //Value for player2 strength:
		thing.set(attribute, Integer.valueOf(value));
	}

}
