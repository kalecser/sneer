package sneer.bricks.softwaresharing.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.tree.TreeNode;

import sneer.bricks.skin.image.ImageFactory;
import sneer.bricks.softwaresharing.BrickHistory;
import sneer.bricks.softwaresharing.BrickVersion;
import sneer.bricks.softwaresharing.BrickHistory.Status;

class BrickHistoryTreeNode extends AbstractTreeNodeWrapper<BrickVersion> {

	private final BrickHistory _brickHistory;
	private static final ImageIcon _currentBrick = loadIcon("currentBrick.png");
	private static final ImageIcon _rejectedBrick = loadIcon("rejectedBrick.png");
	
	private static final ImageIcon _newBrick = loadIcon("newBrick.png");
	private static final ImageIcon _differentBrick = loadIcon("differentBrick.png");
	private static final ImageIcon _divergingBrick = loadIcon("divergingBrick.png");

	private static final ImageIcon _addNewBrick = loadIcon("addNewBrick.png");
	private static final ImageIcon _addDifferentBrick = loadIcon("addDifferentBrick.png");
	private static final ImageIcon _addDivergingBrick = loadIcon("addDivergingBrick.png");

	private static ImageIcon loadIcon(String fileName){
		return my(ImageFactory.class).getIcon(BrickHistoryTreeNode.class, fileName);
	}
	
	BrickHistoryTreeNode(TreeNode parent, BrickHistory brickHistory){
		super(parent, brickHistory);
		_brickHistory = brickHistory;
		
		getIcon();
	}
	
	@Override
	public BrickHistory sourceObject() {
		return _brickHistory;
	}

	@Override public ImageIcon getIcon() {
		if(_brickHistory.status() == Status.DIFFERENT ) {
			if(Util.isBrickStagedForExecution(_brickHistory))
				return _addDifferentBrick;
			
			return _differentBrick;
		}
		
		if(_brickHistory.status() == Status.DIVERGING ){
			if(Util.isBrickStagedForExecution(_brickHistory))
				return _addDivergingBrick;

			return _divergingBrick;
		}
		
		if(_brickHistory.status() == Status.NEW ) {
			if(Util.isBrickStagedForExecution(_brickHistory))
				return _addNewBrick;

			return  _newBrick;
		}
		
		if(_brickHistory.status() == Status.REJECTED ) 
			return _rejectedBrick;
		
		return _currentBrick;
	}
	
	
	@Override public String toString() {
		return _brickHistory.isSnapp()
			? _brickHistory.name() + " (Snapp)"
			: _brickHistory.name();
	}

	
	@SuppressWarnings("rawtypes")
	@Override protected AbstractTreeNodeWrapper wrapChild(int childIndex) {
		return new BrickVersionTreeNode(this, listChildren().get(childIndex));
	}

	@Override protected List<BrickVersion> listChildren() { 
		Collections.sort(_brickHistory.versions(), new Comparator<BrickVersion>(){ @Override public int compare(BrickVersion v1, BrickVersion v2) {
			if(v1.publicationDate()==v2.publicationDate())
				return usersCount(v1) - usersCount(v2);
			
			return (int)(v1.publicationDate()-v2.publicationDate());
		}

		private int usersCount(BrickVersion v1) {
			return v1.users().size();
		}});
		return _brickHistory.versions(); 
	}
}
