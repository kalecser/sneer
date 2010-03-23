package sneer.bricks.network.computers.ips.keeper;

import sneer.bricks.network.social.Contact;

public interface InternetAddress {

	Contact contact();
	
	String host();
	
	int port();
}
