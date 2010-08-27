package sneer.bricks.softwaresharing.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.ImageIcon;

import sneer.bricks.softwaresharing.BrickHistory;
import sneer.bricks.softwaresharing.BrickSpace;

class RootTreeNode extends AbstractTreeNodeWrapper<BrickHistory> {

	private List<BrickHistory> _infos;

	RootTreeNode(List<BrickHistory> infos) {
		super(null, null);
		_infos = infos;
		sortBricks();
	}

	RootTreeNode() {
		super(null, null);
		load();		
	}

	void load() {
		_infos = new ArrayList<BrickHistory>();
		Collection<BrickHistory> currentElements = my(BrickSpace.class).availableBricks();
		_infos.addAll(currentElements);
		sortBricks();
	}

	private void sortBricks() {
		Comparator<BrickHistory> comparator = new Comparator<BrickHistory>(){ @Override public int compare(BrickHistory brick1, BrickHistory brick2) {
			if(brick1.status().ordinal()==brick2.status().ordinal())
				return brick1.name().compareTo(brick2.name());
		
			return brick1.status().ordinal()-brick2.status().ordinal();
		}};
		Collections.sort(_infos, comparator );
	}
	
	@Override public String toString() {  return "root"; }
	
	@Override protected List<BrickHistory> listChildren() { 

		return _infos; 
	}
	@SuppressWarnings("unchecked")
	@Override protected AbstractTreeNodeWrapper wrapChild(int childIndex) {
		return new BrickHistoryTreeNode(this, listChildren().get(childIndex));
	}

	@Override public ImageIcon getIcon() { return null; }
}
