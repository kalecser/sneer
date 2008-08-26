package main;

import snapps.contacts.ContactsSnapp;
import snapps.owner.OwnerSnapp;
import sneer.kernel.container.Container;
import sneer.kernel.container.ContainerUtils;
import sneer.pulp.contacts.ContactManager;
import sneer.skin.dashboard.Dashboard;

public class MainDemo {

	static int y = 10;

	public static void main(String[] args) throws Exception {

		Container container = ContainerUtils.getContainer();

		container.produce(OwnerSnapp.class);

		ContactManager contacts = container.produce(ContactManager.class);
		contacts.addContact("Sandro");
		contacts.addContact("Klaus");
		contacts.addContact("Bamboo");
		contacts.addContact("Nell");
		
		container.produce(ContactsSnapp.class);
		
		container.produce(Dashboard.class);
	}
}
