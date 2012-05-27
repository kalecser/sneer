package sneer.bricks.snapps.games.go.impl;

import static basis.environments.Environments.my;

import javax.swing.JOptionPane;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.gui.guithread.GuiThread;
import sneer.bricks.identity.name.OwnName;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.snapps.contacts.actions.ContactAction;
import sneer.bricks.snapps.contacts.actions.ContactActionManager;
import sneer.bricks.snapps.contacts.gui.ContactsGui;
import sneer.bricks.snapps.games.go.GoMain;
import sneer.bricks.snapps.games.go.impl.gui.GuiPlayer;
import sneer.bricks.snapps.games.go.impl.logging.GoLogger;
import sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor;
import sneer.bricks.snapps.games.go.impl.logic.Move;
import sneer.bricks.snapps.games.go.impl.network.GoInvitation;
import sneer.bricks.snapps.games.go.impl.network.GoMessage;
import sneer.bricks.snapps.games.go.impl.network.GoMove;
import sneer.bricks.snapps.games.go.impl.sneerSpecifics.RemotePlayerOnSneer;
import sneer.bricks.snapps.games.go.impl.sneerSpecifics.SneerTimerFactory;
import basis.lang.ByRef;
import basis.lang.Closure;
import basis.lang.Consumer;

class GoMainImpl implements GoMain {

	private Register<Move> _moveRegister = my(Signals.class).newRegister(null);

	@SuppressWarnings("unused") private WeakContract _refToAvoidGc;
	@SuppressWarnings("unused") private WeakContract _refToAvoidGc2;

	private Move _remoteMove;

	private Seal _adversary;

	{
		setupContextMenu();
		subscribeToReceiveGameMessages();
	}

	private void subscribeToReceiveGameMessages() {
		_refToAvoidGc = my(TupleSpace.class).addSubscription(GoMessage.class, new Consumer<GoMessage>() { @Override public void consume(final GoMessage message) {
			if (message instanceof GoInvitation) { // Why not use only GoMessage with a string field (for handshake and moves exchange)?
				handleInviation((GoInvitation)message);
			}
			
			if (message instanceof GoMove) {
				handleMove((GoMove)message);
			}
		}});
	}

	private void setupContextMenu() {
		my(ContactActionManager.class).addContactAction(new ContactAction() {
			@Override
			public void run() {
				Contact contact = my(ContactsGui.class).selectedContact().currentValue();
				_adversary = my(ContactSeals.class).sealGiven(contact).currentValue();
				
				my(TupleSpace.class).add(
					new GoInvitation(_adversary, "Wanna play Go with " + my(Attributes.class).myAttributeValue(OwnName.class)  + "?")
				);
				
			}

			@Override public String caption() { return "Toroidal Go"; }
			@Override public boolean isVisible() { return true; }
			@Override public boolean isEnabled() { return true; }
			@Override public int positionInMenu() { return 400; }

		});
	}

	private void handleInviation(final GoInvitation message) {
		
		final ByRef<StoneColor> stoneColor = ByRef.newInstance();

		if(message.publisher.equals(my(OwnSeal.class).get().currentValue())) {
			stoneColor.value = StoneColor.WHITE;
		} else {
			_adversary = message.publisher;
			stoneColor.value = StoneColor.BLACK;
			int response = JOptionPane.showConfirmDialog(null, message.text, "Let's play Go", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (response != JOptionPane.YES_OPTION) return;	
		}

		setupPlayers(stoneColor);
		
		_refToAvoidGc2 = _moveRegister.output().addReceiver(new Consumer<Move>() { @Override public void consume(Move move) {
			GoLogger.log("received ("+move+")");
			if(move == null ){
				GoLogger.log("move is null");
				return;
			}
			if(move.equals(_remoteMove)){
				GoLogger.log("move is equals _remoteMove");
				return;	
			}
			my(TupleSpace.class).add(new GoMove(_adversary, move));
		}});
	}

	private void setupPlayers(final ByRef<StoneColor> stoneColor) {
		//			my(TimeboxedEventQueue.class).startQueueing(5000); // Fix: Talk to Klaus about Timebox issue
		my(GuiThread.class).invokeAndWaitForWussies(new Closure(){@Override public void run() {
			Player remotePlayer = new RemotePlayerOnSneer(_moveRegister);
			Player localPlayer = new GuiPlayer(stoneColor.value, 0, new SneerTimerFactory()); 
			localPlayer.setAdversary(remotePlayer);
			remotePlayer.setAdversary(localPlayer);
		}});
	}	
	 
	private void handleMove(GoMove message) {
		if(message.publisher.equals(my(OwnSeal.class).get().currentValue()))
			return;
		GoLogger.log("GoMainImpl.handleMove("+message+")");
		_remoteMove = message.move;
		_moveRegister.setter().consume(message.move);
	}

}
