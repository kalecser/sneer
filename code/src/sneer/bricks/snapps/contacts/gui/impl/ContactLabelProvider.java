/**
 * 
 */
package sneer.bricks.snapps.contacts.gui.impl;

import static basis.environments.Environments.my;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sneer.bricks.hardware.gui.images.Images;
import sneer.bricks.hardware.ram.collections.CollectionUtils;
import sneer.bricks.network.computers.connections.ConnectionManager;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.gates.strings.StringGates;
import sneer.bricks.skin.widgets.reactive.LabelProvider;
import sneer.bricks.snapps.contacts.gui.ContactTextProvider;
import basis.lang.Functor;

final class ContactLabelProvider implements LabelProvider<Contact> {

		static private final Image ONLINE = getImage("online.png");
		static private final Image OFFLINE = getImage("offline.png");

		private List<ContactTextProvider> _contactTextProviders = new ArrayList<ContactTextProvider>();

		@Override
		public Signal<Image> imageFor(final Contact contact) {
			Signal<Boolean> isOnline = my(ConnectionManager.class).connectionFor(contact).isConnected();
			return my(Signals.class).adapt(isOnline, new Functor<Boolean, Image>(){ @Override public Image evaluate(Boolean value) {
				return value ? ONLINE : OFFLINE;
			}});
		}

		
		@Override public Signal<String> textFor(final Contact contact) {
			Signal<String>[] texts = my(CollectionUtils.class).map(_contactTextProviders, new Functor<ContactTextProvider, Signal<String>>() { @Override public Signal<String> evaluate(ContactTextProvider textProvider) throws RuntimeException {
				return textProvider.textFor(contact);
			}}).toArray(new Signal[0]);

			return my(StringGates.class).concat(" ", texts);
		}
		
		
		static private Image getImage(String key) {
			return my(Images.class).getImage(ContactsGuiImpl.class.getResource(key));
		}


		void register(ContactTextProvider textProvider) {
			_contactTextProviders.add(textProvider);
			Collections.sort(_contactTextProviders, new Comparator<ContactTextProvider>() { @Override public int compare(ContactTextProvider provider1, ContactTextProvider provider2) {
				return provider1.position().compareTo(provider2.position());
			}});
		}

	}