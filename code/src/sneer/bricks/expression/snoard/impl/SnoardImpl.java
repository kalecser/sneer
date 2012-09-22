package sneer.bricks.expression.snoard.impl;

import static basis.environments.Environments.my;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import sneer.bricks.expression.snoard.Snoard;
import sneer.bricks.expression.snoard.SnoardTuple;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.gui.actions.Action;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.snapps.contacts.actions.ContactAction;
import sneer.bricks.snapps.contacts.actions.ContactActionManager;
import sneer.bricks.snapps.contacts.gui.ContactsGui;
import basis.lang.Consumer;

public class SnoardImpl implements Snoard, ClipboardOwner {
	private WeakContract subscription;
	private ArrayBlockingQueue<Light> lights = new ArrayBlockingQueue<>(10);
	
	public SnoardImpl() {
		my(ContactActionManager.class).addContactAction(new ContactAction() {

			@Override
			public String caption() {
				return "Send clipboard";
			}

			@Override
			public void run() {
				sendClipboardContents(my(ContactsGui.class).selectedContact()
						.currentValue());
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
				return 1;
			}
		});

		subscription = my(TupleSpace.class).addSubscription(SnoardTuple.class,
				new Consumer<SnoardTuple>() {

					@Override
					public void consume(SnoardTuple value) {
						receiveClipboard(value);
					}
					
				});
	}

	protected void sendClipboardContents(Contact contact) {
		SnoardTuple t = new SnoardTuple(getClipboardContents(), my(ContactSeals.class)
				.sealGiven(contact).currentValue());
		my(TupleSpace.class).add(t);
	}

	public String getClipboardContents() {
		String result = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		// odd: the Object param of getContents is not currently used
		Transferable contents = clipboard.getContents(null);
			
		boolean hasTransferableText = (contents != null)
				&& contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		if (hasTransferableText) {
			try {
				result = (String) contents
						.getTransferData(DataFlavor.stringFlavor);
			} catch (UnsupportedFlavorException ex) {
				// highly unlikely since we are using a standard DataFlavor
				System.out.println(ex);
				ex.printStackTrace();
			} catch (IOException ex) {
				System.out.println(ex);
				ex.printStackTrace();
			}
		}
		return result;
	}

	public void setClipboardContents(String aString) {
		StringSelection stringSelection = new StringSelection(aString);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, this);
	}
	
	private void receiveClipboard(final SnoardTuple value) {
		if (!value.addressee.equals(my(OwnSeal.class).get().currentValue()))
			return;
		
		String from = my(ContactSeals.class).contactGiven(value.publisher).nickname().currentValue();
		
		final Light blinkingLight = my(BlinkingLights.class).turnOn(LightType.GOOD_NEWS, "Snoard from "+from, value.clipValue.toString());
		
		lights.add(blinkingLight);
		if (lights.size() > 4) {
			Light light = lights.poll();
			if (light != null) 
				my(BlinkingLights.class).turnOffIfNecessary(light);
		}
		
		blinkingLight.addAction(new Action() {
			
			@Override
			public void run() {
				setClipboardContents((String) value.clipValue);
				my(BlinkingLights.class).turnOffIfNecessary(blinkingLight);
			}
			
			@Override
			public String caption() {
				return "Accept";
			}
		});
		
		blinkingLight.addAction(new Action() {
			
			@Override
			public void run() {
				my(BlinkingLights.class).turnOffIfNecessary(blinkingLight);
			}
			
			@Override
			public String caption() {
				return "Reject";
			}
		});
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// ignoring...
	}

}
