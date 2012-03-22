package dfcsantos.music.ui.presenter.contactstatus.impl;

import static basis.environments.Environments.my;
import basis.lang.Functor;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.snapps.contacts.gui.ContactTextProvider;
import sneer.bricks.snapps.contacts.gui.ContactsGui;
import dfcsantos.music.notification.playingtrack.PlayingTrack;
import dfcsantos.music.ui.presenter.contactstatus.ContactMusicStatus;



class ContactMusicStatusImpl implements ContactMusicStatus, ContactTextProvider {

	private static final String MUSICAL_NOTE_ICON = "\u266A"; 

    {
		my(ContactsGui.class).registerContactTextProvider(this);
	}

    
	@Override public Position position() {
		return ContactTextProvider.Position.RIGHT; 
	}

	
	@Override
	public Signal<String> textFor(Contact contact) {
		return my(Signals.class).adapt(playingTrackFor(contact), new Functor<String, String>() { @Override public String evaluate(String playingTrack) {
			return withMusicalNote(playingTrack);
		}});
	}

    
	private String withMusicalNote(String playingTrack) {
		if (playingTrack == null) return "";
		if (playingTrack.isEmpty()) return "";
		return MUSICAL_NOTE_ICON + " " + playingTrack;
	}
	
	
	private Signal<String> playingTrackFor(Contact contact) {
		return my(Attributes.class).attributeValueFor(contact, PlayingTrack.class, String.class);
	}

}
