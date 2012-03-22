package sneer.bricks.softwaresharing.gui.impl;

import static basis.environments.Environments.my;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.tree.TreeNode;

import basis.lang.exceptions.NotImplementedYet;

import sneer.bricks.skin.image.ImageFactory;

class StringTreeNode extends AbstractTreeNodeWrapper<Object> {

	private final static List<Object> _empty = new ArrayList<Object>();
	private final String _name;
	
	private static final ImageIcon _users = loadIcon("users.png");

	private static ImageIcon loadIcon(String fileName){
		return my(ImageFactory.class).getIcon(BrickHistoryTreeNode.class, fileName);
	}
	StringTreeNode(TreeNode parent, String name) {
		super(parent, null);
		_name = name;
	}

	@Override protected List<Object> listChildren() {return _empty;}
	@Override protected AbstractTreeNodeWrapper<Object> wrapChild(int childIndex) {throw new NotImplementedYet();}
	@Override public String toString() {return _name;	}
	@Override public ImageIcon getIcon() { return _users; }
}
