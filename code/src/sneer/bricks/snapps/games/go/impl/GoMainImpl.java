package sneer.bricks.snapps.games.go.impl;

import static sneer.foundation.environments.Environments.my;

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
import sneer.bricks.snapps.games.go.GoBoard.StoneColor;
import sneer.bricks.snapps.games.go.GoInvitation;
import sneer.bricks.snapps.games.go.GoMain;
import sneer.bricks.snapps.games.go.GoMessage;
import sneer.bricks.snapps.games.go.GoMove;
import sneer.bricks.snapps.games.go.Move;
import sneer.bricks.snapps.games.go.gui.GoFrame;
import sneer.foundation.lang.ByRef;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;

class GoMainImpl implements GoMain {

	private Register<Move> _moveRegister = my(Signals.class).newRegister(null);

	@SuppressWarnings("unused") private final WeakContract _refToAvoidGc;
	@SuppressWarnings("unused") private WeakContract _refToAvoidGc2;

	private Move _remoteMove;

	private Seal _adversary;

	{
		_refToAvoidGc = my(TupleSpace.class).addSubscription(GoMessage.class, new Consumer<GoMessage>() { @Override public void consume(final GoMessage message) {
			if (message instanceof GoInvitation) { // Why not use only GoMessage with a string field (for handshake and moves exchange)?
				handleInviation((GoInvitation)message);
			}
			
			if (message instanceof GoMove) {
				handleMove((GoMove)message);
			}
			
		}
		});

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

//			my(TimeboxedEventQueue.class).startQueueing(5000); // Fix: Talk to Klaus about Timebox issue
		my(GuiThread.class).invokeAndWaitForWussies(new Closure(){@Override public void run() {
			new GoFrame(_moveRegister, stoneColor.value, 0);
		}});
		
		_refToAvoidGc2 = _moveRegister.output().addReceiver(new Consumer<Move>() { @Override public void consume(Move move) {
			if(move == null ) return;
			if(move.equals(_remoteMove)) return;	
			my(TupleSpace.class).add(new GoMove(_adversary, move));
		}});
	}	
	
	private void handleMove(GoMove message) {
		if(message.publisher.equals(my(OwnSeal.class).get().currentValue()))
			return;
		System.out.println(message);
		_remoteMove = message.move;
		_moveRegister.setter().consume(message.move);
	}

}
