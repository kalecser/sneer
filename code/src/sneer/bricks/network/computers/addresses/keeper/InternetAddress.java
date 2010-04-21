package sneer.bricks.network.computers.addresses.keeper;

import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;

public interface InternetAddress {

	Contact contact();
	
	String host();
	
	Signal<Integer> port(); //Ugly
}
