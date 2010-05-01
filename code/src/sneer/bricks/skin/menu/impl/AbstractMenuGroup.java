package sneer.bricks.skin.menu.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JComponent;
import javax.swing.JMenuItem;

import sneer.bricks.hardware.gui.actions.Action;
import sneer.bricks.skin.main.synth.menu.SynthMenus;
import sneer.bricks.skin.menu.MenuGroup;

public abstract class AbstractMenuGroup<T extends JComponent> implements MenuGroup<T> {

	private Map<Integer, JMenuItem> _menuItemsByIndex = new TreeMap<Integer, JMenuItem>();

	@Override
	public void addAction(final String caption, final Runnable delegate) {
		addAction(caption, delegate, null);
	}

	@Override
	public void addAction(final String caption, final Runnable delegate, Integer index) {
		addAction(new Action(){
			@Override
			public String caption() {
				return caption;
			}

			@Override
			public void run() {
				delegate.run();
			}
		}, index);
	}

	@Override
	public void addAction(final Action action) {
		addAction(action, null);
	}

	@Override
	public void addAction(final Action action, Integer index) {
		final JMenuItem menuItem = my(SynthMenus.class).createMenuItem();
		menuItem.setText(action.caption());
		addMenuItem(action, menuItem, index);
		menuItem.addPropertyChangeListener(new PropertyChangeListener(){ @Override public void propertyChange(PropertyChangeEvent evt) {
			menuItem.setText(action.caption());
		}});
	}

	@Override
	public void addGroup(MenuGroup<? extends JComponent> group) {
		getWidget().add(group.getWidget());
	}

	synchronized
	private void addMenuItem(final Action action, final JMenuItem menuItem, Integer index) {
		menuItem.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent ignored) {
			action.run();
		}});
		if (index != null) {
			_menuItemsByIndex.put(index, menuItem);
			rebuildMenu();
		} else {
			getWidget().add(menuItem);
		}
	}

	private void rebuildMenu() {
		removeAllIntems();
		insertItemsInAscendingOrder();
	}

	private void removeAllIntems() {
		getWidget().removeAll();
	}

	private void insertItemsInAscendingOrder() {
		// _menuItemsByIndex keeps the menu items sorted by their given index
		for (JMenuItem menuItem : _menuItemsByIndex.values())
			getWidget().add(menuItem);
	}

}
