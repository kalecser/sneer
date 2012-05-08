package sneer.bricks.snapps.contacts.gui.impl;

import static basis.environments.Environments.my;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import basis.lang.Consumer;

import sneer.bricks.hardware.gui.actions.Action;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.network.social.heartbeat.stethoscope.Stethoscope;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.pulp.reactive.collections.listsorter.ListSorter;
import sneer.bricks.pulp.reactive.signalchooser.SignalChooser;
import sneer.bricks.skin.main.dashboard.InstrumentPanel;
import sneer.bricks.skin.main.instrumentregistry.InstrumentRegistry;
import sneer.bricks.skin.menu.MenuFactory;
import sneer.bricks.skin.menu.MenuGroup;
import sneer.bricks.skin.popuptrigger.PopupTrigger;
import sneer.bricks.skin.widgets.reactive.ListWidget;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.bricks.snapps.contacts.actions.ContactAction;
import sneer.bricks.snapps.contacts.actions.ContactActionManager;
import sneer.bricks.snapps.contacts.gui.ContactTextProvider;
import sneer.bricks.snapps.contacts.gui.ContactsGui;
import sneer.bricks.snapps.contacts.gui.comparator.ContactComparator;

class ContactsGuiImpl implements ContactsGui {

	private final ListWidget<Contact> _contactList;

	private Container _container;

	private final ContactLabelProvider _labelProvider = new ContactLabelProvider();

	
	ContactsGuiImpl() {
		registerContactTextProvider(new ContactTextProvider() {
				@Override public Position position() { return ContactTextProvider.Position.CENTER; }
				@Override public Signal<String> textFor(Contact contact) { return contact.nickname(); }
			}
		);

		final ListSignal<Contact> _sortedList = my(ListSorter.class).sort(my(Contacts.class).contacts() , my(ContactComparator.class), new SignalChooser<Contact>() { @Override public Signal<?>[] signalsToReceiveFrom(Contact contact) {
			return new Signal<?>[] { my(Stethoscope.class).isAlive(contact), contact.nickname() };
		}});

		_contactList = my(ReactiveWidgetFactory.class).newList(_sortedList, _labelProvider);

		my(InstrumentRegistry.class).registerInstrument(this);
	} 

	@Override
	public void init(InstrumentPanel window) {
		_container = window.contentPane();
		my(ContactActionManager.class).setBaseComponent(_container);

		_contactList.getComponent().setName("ContactList");
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().add(_contactList.getComponent());
		
		_container.setLayout(new BorderLayout());
		_container.add(scrollPane, BorderLayout.CENTER);
		
		addContactActions(window.actions());
		addDefaultContactAction();
		
		new ListContactsPopUpSupport();
	}

	private void addDefaultContactAction() {
		contactList().addMouseListener(new MouseAdapter(){ @Override public void mouseReleased(MouseEvent e) {
			if (e.getClickCount() > 1)
				my(ContactActionManager.class).defaultAction().run();
		}});
	}

	@Override
	public int defaultHeight() {
		return 144;
	}
	
	@Override
	public String title() {
		return "My Contacts";
	}
	
	@Override
	public Signal<Contact> selectedContact(){
		return _contactList.selectedElement();
	}
	
	private void addContactActions(MenuGroup<? extends JComponent> menuGroup) {
		menuGroup.addAction(-100, new Action() {
				@Override public String caption() { return "New Contact..."; }
				@Override public void run() {
					contactList().setSelectedValue(newContact(), true);
				}});
	}
	
	private Contact newContact() {
		return my(Contacts.class).produceContact("<New Contact>");
	}

	private JList<Object> contactList() {
		return (JList<Object>)_contactList.getComponent();
	}	

	private final class ListContactsPopUpSupport {
		private ListContactsPopUpSupport() {
			final JList<Object> list = _contactList.getMainWidget();
			my(PopupTrigger.class).listen(list, new Consumer<MouseEvent>(){ @Override public void consume(MouseEvent e) {
				tryToShowContactMenu(e);
			}});
		}
		
		private void tryToShowContactMenu(MouseEvent e) {
			JList<Object> list = _contactList.getMainWidget();
			int index = list.locationToIndex(e.getPoint());
			list.getSelectionModel().setSelectionInterval(index, index);
			if (!e.isPopupTrigger()) return;
			
			MenuGroup<JPopupMenu> popupMain = my(MenuFactory.class).createPopupMenu();
			for (ContactAction action : my(ContactActionManager.class).actions())
				if (action.isVisible())
					popupMain.addAction(action.positionInMenu(), action);

			if (popupMain.getWidget().getSubElements().length>0)
				popupMain.getWidget().show(e.getComponent(),e.getX(),e.getY());
		}
	}

	@Override
	public void registerContactTextProvider(ContactTextProvider textProvider) {
		_labelProvider.register(textProvider);
	}

}