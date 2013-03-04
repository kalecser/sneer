package sneer.bricks.snapps.games.sliceWars.impl;

import static basis.environments.Environments.my;

import java.util.Random;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.snapps.contacts.actions.ContactAction;
import sneer.bricks.snapps.contacts.actions.ContactActionManager;
import sneer.bricks.snapps.contacts.gui.ContactsGui;
import sneer.bricks.snapps.games.sliceWars.SliceWarsMain;
import sneer.bricks.snapps.games.sliceWars.impl.gui.GuiPlayer;
import sneer.bricks.snapps.games.sliceWars.impl.logic.Player;
import sneer.bricks.snapps.games.sliceWars.impl.sneer.SliceWarsInvitation;
import sneer.bricks.snapps.games.sliceWars.impl.sneer.SliceWarsPlay;
import sneer.bricks.snapps.games.sliceWars.impl.sneer.SneerPlayer;
import basis.lang.Consumer;

public class SliceWarsMainImpl implements SliceWarsMain {

	private GuiPlayer _guiPlayer;
	
	@SuppressWarnings("unused") private WeakContract _refToAvoidGc2;
	@SuppressWarnings("unused") private WeakContract _refToAvoidGc;
	
	{
		setupContextMenu();
		subscribeToReceiveSliceWarsMessages();
	}
	
	private void setupContextMenu() {
		my(ContactActionManager.class).addContactAction(new ContactAction() {
			@Override public void run() {
				int seed = new Random().nextInt();
				Contact contact = my(ContactsGui.class).selectedContact().currentValue();
				Seal addressee = my(ContactSeals.class).sealGiven(contact).currentValue();
				SliceWarsInvitation sliceWarsInvitation = new SliceWarsInvitation(addressee, seed);
				my(TupleSpace.class).add(sliceWarsInvitation);
				SneerPlayer sneerPlayer = new SneerPlayer(addressee);
				_guiPlayer = new GuiPlayer(new Player(1, 2), sneerPlayer, seed, 2, 6, 6, 12);
			}

			@Override public String caption() { return "Slice wars"; }
			@Override public boolean isVisible() { return true; }
			@Override public boolean isEnabled() { return true; }
			@Override public int positionInMenu() { return 400; }

		});
	}
	
	private void subscribeToReceiveSliceWarsMessages() {
		_refToAvoidGc = my(TupleSpace.class).addSubscription(SliceWarsInvitation.class, new Consumer<SliceWarsInvitation>() { @Override public void consume(final SliceWarsInvitation invitation) {
			if(invitation.publisher.equals(my(OwnSeal.class).get().currentValue())) {
				return;
			} 
			SneerPlayer sneerPlayer = new SneerPlayer(invitation.publisher);
			_guiPlayer = new GuiPlayer(new Player(2, 2), sneerPlayer, invitation.seed, 2, 6, 6, 12);
		}});
		
		_refToAvoidGc2 = my(TupleSpace.class).addSubscription(SliceWarsPlay.class, new Consumer<SliceWarsPlay>() { @Override public void consume(final SliceWarsPlay play) {
			_guiPlayer.play(play.remotePlay);
		}});
		
	}
}
