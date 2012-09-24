package snype.whisper.gui.impl;

import static basis.environments.Environments.my;

import java.util.concurrent.ConcurrentHashMap;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.gui.actions.Action;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.snapps.contacts.actions.ContactAction;
import sneer.bricks.snapps.contacts.actions.ContactActionManager;
import sneer.bricks.snapps.contacts.gui.ContactsGui;
import snype.whisper.gui.SnypeAcceptTuple;
import snype.whisper.gui.SnypeGUI;
import snype.whisper.gui.SnypeStartTuple;
import snype.whisper.gui.SnypeStopTuple;
import snype.whisper.skin.audio.mic.Mic;
import snype.whisper.speextuples.SpeexTuples2;
import basis.lang.Consumer;

public class SnypeGUIImpl implements SnypeGUI {

	@SuppressWarnings("unused") private WeakContract acceptSubscription = my(RemoteTuples.class).addSubscription(SnypeAcceptTuple.class, new Consumer<SnypeAcceptTuple>() {

		@Override
		public void consume(SnypeAcceptTuple tuple) {
			Contact contact = my(ContactSeals.class).contactGiven(tuple.publisher);
			createCallLightIfNeeded(contact, "Snype with " + contact.nickname().currentValue(), "Press stop to terminate the call");
			setupConnection(tuple.publisher);
		}
	});

	@SuppressWarnings("unused")
	private WeakContract stopSubscription = my(RemoteTuples.class).addSubscription(SnypeStopTuple.class, new Consumer<SnypeStopTuple>() {

		@Override
		public void consume(SnypeStopTuple tuple) {
			Contact contact = my(ContactSeals.class).contactGiven(tuple.publisher);
			Light light = lightsByContact.get(contact);
			if(light!=null) {
				my(BlinkingLights.class).turnOffIfNecessary(light);
			}
			tearDownConnection(tuple.publisher);
	}});
	
	@SuppressWarnings("unused")
	private WeakContract startSubscription = my(RemoteTuples.class).addSubscription(SnypeStartTuple.class, new Consumer<SnypeStartTuple>() {

		@Override
		public void consume(final SnypeStartTuple tuple) {
			Contact contact = my(ContactSeals.class).contactGiven(tuple.publisher);
			final Light light = my(BlinkingLights.class).turnOn(LightType.INFO, "Call from " + contact.nickname().currentValue(), "Awaiting acceptance");
			light.addAction(new Action() {
				
				@Override
				public void run() {
					my(BlinkingLights.class).turnOffIfNecessary(light);
					my(TupleSpace.class).add(new SnypeAcceptTuple(tuple.publisher));
					Contact contact = my(ContactSeals.class).contactGiven(tuple.publisher);
					createCallLightIfNeeded(contact, "Snype with " + contact.nickname().currentValue(), "Press stop to terminate the call");
					setupConnection(tuple.publisher);
				}
				

				@Override public String caption() { return "Accept"; }
			});
			
			light.addAction(new Action() {
				
				@Override
				public void run() {
					my(BlinkingLights.class).turnOffIfNecessary(light);
					my(TupleSpace.class).add(new SnypeStopTuple(tuple.publisher));
				}
				
				@Override public String caption() { return "Reject"; }
			});
		}});

	private ConcurrentHashMap<Contact, Light> lightsByContact= new ConcurrentHashMap<>();
	
	{
		my(ContactActionManager.class).addContactAction(new ContactAction() {

			@Override public String caption() { return "Snype call"; }

			@Override public void run() { startCall(); }

			@Override public boolean isVisible() { return true; }

			@Override public boolean isEnabled() { return true; }

			@Override public int positionInMenu() { return 2; }
		});
		
		

	}

	protected void startCall() {
		Contact contact = my(ContactsGui.class).selectedContact().currentValue();
		Seal seal = my(ContactSeals.class).sealGiven(contact).currentValue();
		my(TupleSpace.class).add(new SnypeStartTuple(seal));
		createCallLightIfNeeded(contact, "Calling to " + contact.nickname().currentValue(), "Awaiting acceptance");
	}

	private void createCallLightIfNeeded(final Contact contact, String caption, String helpMessage) {
		if (!lightsByContact.containsKey(contact)) {
			final Light light = my(BlinkingLights.class).prepare(LightType.INFO);
			
			light.addAction(new Action() {
				
				@Override
				public void run() {
					Seal seal = my(ContactSeals.class).sealGiven(contact).currentValue();
					my(BlinkingLights.class).turnOffIfNecessary(light);
					my(TupleSpace.class).add(new SnypeStopTuple(seal));
					tearDownConnection(seal);
				}
	
				@Override public String caption() { return "Stop"; }
			});
			
			lightsByContact.put(contact, light);
		}
		
		my(BlinkingLights.class).turnOffIfNecessary(lightsByContact.get(contact));
		my(BlinkingLights.class).turnOnIfNecessary(lightsByContact.get(contact), caption, helpMessage);
	}
	
	protected void setupConnection(Seal other) {
		my(SpeexTuples2.class).addTalker(other);
		my(Mic.class).open();
	}

	protected void tearDownConnection(Seal other) {
		my(SpeexTuples2.class).removeTalker(other);
		if(!my(SpeexTuples2.class).hasTalkers())
			my(Mic.class).close();
	}
}
