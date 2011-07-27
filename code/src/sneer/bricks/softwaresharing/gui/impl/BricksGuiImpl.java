package sneer.bricks.softwaresharing.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.skin.image.ImageFactory;
import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.skin.windowboundssetter.WindowBoundsSetter;
import sneer.bricks.snapps.diff.text.gui.TextDiffPanel;
import sneer.bricks.snapps.diff.text.gui.TextDiffPanels;
import sneer.bricks.snapps.system.log.gui.LogConsole;
import sneer.bricks.softwaresharing.BrickHistory;
import sneer.bricks.softwaresharing.BrickSpace;
import sneer.bricks.softwaresharing.BrickVersion;
import sneer.bricks.softwaresharing.BrickVersion.Status;
import sneer.bricks.softwaresharing.FileVersion;
import sneer.bricks.softwaresharing.gui.BricksGui;
import sneer.bricks.softwaresharing.stager.BrickStager;
import sneer.foundation.lang.Closure;

class BricksGuiImpl extends JFrame implements BricksGui {

	private static final JToggleButton _selectButton = new JToggleButton(loadIcon("add.png"));
	private static final JToggleButton _rejectButton = new JToggleButton(loadIcon("rejectedVersion.png"));
	private final JTree _tree = new JTree();
	private final JList _files = new JList();
	private final TextDiffPanel _diffPanel = my(TextDiffPanels.class).newPanel();
	
	private final Integer _OFFSET_X = 20; //(Integer) _synth.getDefaultProperty("LogConsoleImpl.offsetX");
	private final Integer _OFFSET_Y = 0; //(Integer) _synth.getDefaultProperty("LogConsoleImpl.offsetY");
	private final Integer _HEIGHT = 160; //(Integer) _synth.getDefaultProperty("LogConsoleImpl.height");
	private final Integer _X = 10;  //(Integer) _synth.getDefaultProperty("LogConsoleImpl.x");

	protected Object _lastSelectedNode;
	
	@SuppressWarnings("unused")	private WeakContract _refToAvoidGc;
	private JLabel _loadingLabel;
	private JScrollPane _scrollTree;
	
	private static ImageIcon loadIcon(String fileName){
		return my(ImageFactory.class).getIcon(BricksGuiImpl.class, fileName);
	}
	
	BricksGuiImpl(){
		super("Bricks");
		
		my(LogConsole.class);
		
		initGui(); 
		initListeners();
		registerMainMenuItem();
	}
	
	private void registerMainMenuItem() {
		final WindowBoundsSetter wbSetter = my(WindowBoundsSetter.class);
		wbSetter.runWhenBaseContainerIsReady(new Closure() { @Override public void run() {
			my(MainMenu.class).addAction(20, "Bricks", new Closure() { @Override public void run() {
				show(wbSetter);
			}});
		}});
	}
	
	private void initListeners() {
		_tree.addTreeSelectionListener(new TreeSelectionListener(){ @Override public void valueChanged(TreeSelectionEvent event) {
			_lastSelectedNode = event.getPath().getLastPathComponent();
			tryShowFiles();
			adjustToolbar();
		}});
	
		_files.addListSelectionListener(new ListSelectionListener(){ @Override public void valueChanged(ListSelectionEvent event) {
			tryCompare();
		}});
		
		_selectButton.addActionListener(new ActionListener(){ @Override public void actionPerformed(ActionEvent e) {
			BrickVersion version = selectedBrickVersion();
			selectedBrick().setChosenForExecution(version, !version.isChosenForExecution());
			_selectButton.setSelected(version.isChosenForExecution());
			_tree.repaint();
		}});
		
		_rejectButton.addActionListener(new ActionListener(){ @Override public void actionPerformed(ActionEvent e) {
			BrickVersion version = selectedBrickVersion();
			version.setRejected(version.status().currentValue() != Status.REJECTED);
			_rejectButton.setSelected(version.status().currentValue() == Status.REJECTED);
			_tree.repaint();
		}});
	}

	protected void adjustToolbar() {
		_selectButton.setSelected(false);
		_selectButton.setEnabled(false);
		_rejectButton.setSelected(false);
		_rejectButton.setEnabled(false);
		
		if(!(_lastSelectedNode instanceof BrickVersionTreeNode))
			return;
		
		BrickVersion version = selectedBrickVersion();
		
		if(version.status().currentValue() == Status.CURRENT)
			return;
		
		_selectButton.setEnabled(true);
		_selectButton.setSelected(version.isChosenForExecution());

		_rejectButton.setEnabled(true);
		_rejectButton.setSelected(version.status().currentValue() == Status.REJECTED);
	}

	private BrickVersion selectedBrickVersion() {
		return selectedBrickVersionTreeNode().sourceObject();
	}

	private BrickVersionTreeNode selectedBrickVersionTreeNode() {
		return (BrickVersionTreeNode) _lastSelectedNode;
	}
	
	private BrickHistory selectedBrick() {
		return ((BrickHistoryTreeNode)selectedBrickVersionTreeNode()._parent).sourceObject();
	}

	private void tryShowFiles() {
		if(_tree.getSelectionCount()==0) 
			return;
		
		Object selected = _tree.getSelectionPath().getLastPathComponent();
		if(! (selected instanceof BrickVersionTreeNode)){
			_files.setModel(new DefaultListModel());
			return;
		}
		
		BrickVersionTreeNode node = (BrickVersionTreeNode) selected;
		
		_files.setModel(new FileVersionListModel(node.sourceObject()));
	}	
	
	private void tryCompare() {
		FileVersionWrapper selectedWrapper = (FileVersionWrapper)_files.getSelectedValue();
		if(selectedWrapper==null) {
			_diffPanel.compare("","");
			return;			
		}
		
		FileVersion selected = selectedWrapper.fileVersion();
		byte[] contents = selected.contents();
		_diffPanel.compare(stringFor(selected.contentsInCurrentVersion()), stringFor(contents));
	}

	private String stringFor(byte[] contents) {
		if (contents == null)
			return "";
		return new String(contents);
	}
	
	private void initGui() {

		_tree.setRootVisible(false);
		_tree.setCellRenderer(new BrickTreeCellRenderer());
		_tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		_tree.setBorder(new EmptyBorder(5,5,5,5));
		_tree.setShowsRootHandles(true);
		
		_files.setBorder(new EmptyBorder(5,5,5,5));
		_files.setCellRenderer(new BrickListCellRenderer());

		_selectButton.setEnabled(false);
		_rejectButton.setEnabled(false);
		
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		JToolBar toolbar = new JToolBar();
		contentPane.add(toolbar, BorderLayout.NORTH);
		toolbar.add(_selectButton);
		toolbar.add(_rejectButton);
		
		addMeTooButton(toolbar);
		
		_scrollTree = new JScrollPane();
		JScrollPane scrollFiles = new JScrollPane();
		JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, _scrollTree, scrollFiles);
		
		JScrollPane scrollDiff = new JScrollPane();
		JSplitPane horizontalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, verticalSplit, scrollDiff);
		
		contentPane.add(horizontalSplit, BorderLayout.CENTER);
		
		verticalSplit.setDividerSize(2);
		horizontalSplit.setDividerSize(2);
		verticalSplit.setDividerLocation(300);
		horizontalSplit.setDividerLocation(300);
	
		_loadingLabel = new JLabel("              Loading...");
		_scrollTree.getViewport().add(_loadingLabel);
		
		scrollDiff.getViewport().add(_diffPanel.component());
		scrollFiles.getViewport().add(_files);

		_refToAvoidGc = my(BrickSpace.class).newBuildingFound().addPulseReceiver(new Runnable() { @Override public void run() {
			refreshBrickTree();
		}});
//		refreshBrickTree(); //Uncomment to see mock data.
	}

	private void addMeTooButton(JToolBar toolbar) {
		final JButton button = new JButton("Me Too");
		toolbar.add(button);
		button.addActionListener(new ActionListener(){ @Override public void actionPerformed(ActionEvent e) {
			button.setEnabled(false);
			my(Threads.class).startDaemon("BricksGui MeToo", new Closure() { @Override public void run() {
				try {
					my(BrickStager.class).stageBricksForInstallation();
				} finally {
					button.setEnabled(true);
				}
			}});
		}});
	}
	
	private void show(final WindowBoundsSetter wbSetter) {
		Rectangle unused = wbSetter.unusedArea();
		setBounds(_X , _OFFSET_Y, unused.width-_OFFSET_X, unused.height -_HEIGHT-_OFFSET_Y*2);
		setFocusableWindowState(false);
		setVisible(true);
		setFocusableWindowState(true);
	}

	private void refreshBrickTree() {
		_scrollTree.getViewport().remove(_loadingLabel);
		_scrollTree.getViewport().add(_tree);

		RootTreeNode root = new RootTreeNode();
//		RootTreeNode root = new RootTreeNode(FakeModel.bricks()); //Uncomment to see mock data.
		
		_tree.setModel(new DefaultTreeModel(root));
		_files.setModel(new DefaultListModel());
	}
}