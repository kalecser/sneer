package sneer.snapps.games.drones.ui.impl;

import static basis.environments.Environments.my;
import static java.lang.Integer.parseInt;
import static javax.swing.JOptionPane.showInputDialog;
import static javax.swing.JOptionPane.showMessageDialog;
import static sneer.snapps.games.drones.units.UnitAttribute.ARMOR;
import static sneer.snapps.games.drones.units.UnitAttribute.HITPOINTS;
import static sneer.snapps.games.drones.units.UnitAttribute.STRENGTH;

import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.snapps.contacts.actions.ContactAction;
import sneer.bricks.snapps.contacts.actions.ContactActionManager;
import sneer.bricks.snapps.contacts.gui.ContactsGui;
import sneer.snapps.games.drones.Challenge;
import sneer.snapps.games.drones.ChallengeAcceptance;
import sneer.snapps.games.drones.matches.Match;
import sneer.snapps.games.drones.ui.DronesUI;
import sneer.snapps.games.drones.units.Attributable;
import sneer.snapps.games.drones.units.Unit;
import sneer.snapps.games.drones.units.UnitAttribute;
import basis.lang.Consumer;

class DronesUIImpl implements DronesUI {
	
	private JFrame jFrame;
	private WeakContract timer;
	@SuppressWarnings("unused") private WeakContract refToAvoidGc;
	private WeakContract acceptanceContract;

	{
		my(ContactActionManager.class).addContactAction(new ContactAction(){
			@Override public String caption() { return "Game of Drones"; }
			@Override public void run() { challengeAdversary(); }
			@Override public boolean isVisible() { return true; }
			@Override public boolean isEnabled() { return true; }
			@Override public int positionInMenu() { return 0; }
		});
		
		refToAvoidGc = my(RemoteTuples.class).addSubscription(Challenge.class, new Consumer<Challenge>() { @Override public void consume(Challenge challenge) {
			accept(challenge);
		}});
	}

	private void accept(Challenge challenge) {
		my(TupleSpace.class).add(new ChallengeAcceptance(challenge.publisher, input("Hitpoints"), input("Strength"), input("Armor")));
	}

	private String input(String attribute) {
		return showInputDialog("Value for " + attribute + ":");
	}

	private void challengeAdversary() {
		final Seal adversary = adversarySeal();
		acceptanceContract = my(RemoteTuples.class).addSubscription(ChallengeAcceptance.class, new Consumer<ChallengeAcceptance>() {  @Override public void consume(ChallengeAcceptance acceptance) {
			if (!acceptance.publisher.equals(adversary)) return;
			acceptanceContract.dispose();
			handle(acceptance);
		}});
		my(TupleSpace.class).add(new Challenge(adversary));
	}

	
	private void handle(ChallengeAcceptance acceptance) {
		defineAttributes(acceptance.hitpoints, acceptance.strength, acceptance.armor);
		openFrame();
		startTimer();
	}

	private Seal adversarySeal() {
		Contact adversary = my(ContactsGui.class).selectedContact().currentValue();
		return my(ContactSeals.class).sealGiven(adversary).currentValue();
	}

	private void defineAttributes(String hitpoints, String strength, String armor) {
		defineAttributes(my(Match.class).unit1());
		
		Unit unit2 = my(Match.class).unit2();
		unit2.set(HITPOINTS, parseInt(hitpoints));
		unit2.set(STRENGTH, parseInt(strength));
		unit2.set(ARMOR, parseInt(armor));
	}

	private void startTimer() {
		timer = my(Timer.class).wakeUpNowAndEvery(100, new Runnable() { @Override public void run() {
			if (my(Match.class).isOver()) {
				timer.dispose();
				showResult();
			}
			else
				my(Match.class).step();
				
			jFrame.repaint();
		}});
	}

	private void showResult() {
		JOptionPane.showMessageDialog(null, my(Match.class).result());
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
		try {
			tryToDefine(attribute, thing);
		} catch (NumberFormatException e) {
			showMessageDialog(null, "Error. Try Again");
		}
	}

	private void tryToDefine(UnitAttribute attribute, Attributable thing) throws NumberFormatException {
		String value = showInputDialog("Value for " + thing + " " + attribute + ":"); //Value for player2 strength:
		thing.set(attribute, Integer.valueOf(value));
	}

}
