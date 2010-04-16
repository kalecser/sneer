package sneer.bricks.hardware.io.prevalence.nature.tests.fixtures.impl;

import java.util.ArrayList;
import java.util.List;

import sneer.bricks.hardware.io.prevalence.nature.tests.fixtures.Item;
import sneer.bricks.hardware.io.prevalence.nature.tests.fixtures.SomePrevalentBrick;

class SomePrevalentBrickImpl implements SomePrevalentBrick {

	static final class ItemImpl implements Item {
		private String _name;

		public ItemImpl(String name) {
			_name = name;
		}

		@Override
		public String name() {
			return _name;
		}
	}

	private String _string;
	private List<Item> _items = new ArrayList<Item>();

	@Override
	public String get() {
		return _string;
	}

	@Override
	public void set(String string) {
		_string = string;
	}

	@Override
	public void addItem(String name) {
		Item item = new ItemImpl(name);
		_items.add(item);
	}

	@Override
	public int itemCount() {
		return _items.size();
	}

	@Override
	public void removeItem(Item item) {
		_items.remove(item);
	}

	@Override
	public Item getItem(String name) {
		for (Item item : _items)
			if (item.name().equals(name)) return item;
		return null;
	}
	
}