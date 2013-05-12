package sneer.snapps.games.drones.ui.impl;

import static basis.environments.Environments.my;

import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.snapps.contacts.actions.ContactAction;
import sneer.bricks.snapps.contacts.actions.ContactActionManager;
import sneer.snapps.games.drones.ui.DronesUI;

class DronesUIImpl implements DronesUI {
	
	private JFrame jFrame;
	private Player player1;
	private Player player2;
	private WeakContract refToAvoidGC;

	{
		my(ContactActionManager.class).addContactAction(new ContactAction(){

			@Override
			public String caption() {
				return "Game of Drones";
			}

			@Override
			public void run() {
				open();
			}

			@Override
			public boolean isVisible() {
				return true;
			}

			@Override
			public boolean isEnabled() {
				return true;
			}

			@Override
			public int positionInMenu() {
				return 0;
			}});
	}

	private void open() {
		initFrame();
		initTimer();
	}

	private void initTimer() {
		refToAvoidGC = my(Timer.class).wakeUpNowAndEvery(100, new Runnable() { @Override public void run() {
			jFrame.repaint();
		}});
	}

	private void initFrame() {
		player1 = new Player(0, Player.Direction.RIGHT);
		player2 = new Player(700, Player.Direction.LEFT);
		jFrame = new JFrame("Game of Drones") {
			@Override
			public void paint(Graphics g) {				
				g.clearRect(0,0, jFrame.getWidth(), jFrame.getHeight());
				g.drawRect(player1.x(), 200, 100, 100);
				g.drawRect(player2.x(), 200, 100, 100);
			}		
			
		};
		jFrame.setVisible(true);
		jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		jFrame.setResizable(true);
		jFrame.setBounds(0, 0,800,600);
	}

}
