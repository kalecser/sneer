package sneer.bricks.snapps.system.log.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoundedRangeModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.gui.actions.Action;
import sneer.bricks.hardware.gui.guithread.GuiThread;
import sneer.bricks.hardware.io.log.filter.LogFilter;
import sneer.bricks.hardware.io.log.notifier.LogNotifier;
import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.gates.buffers.assync.AssynchronousBuffers;
import sneer.bricks.skin.main.dashboard.Dashboard;
import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.skin.main.synth.Synth;
import sneer.bricks.skin.main.title.ProcessTitle;
import sneer.bricks.skin.menu.MenuFactory;
import sneer.bricks.skin.menu.MenuGroup;
import sneer.bricks.skin.popuptrigger.PopupTrigger;
import sneer.bricks.skin.widgets.reactive.ListWidget;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.bricks.skin.widgets.reactive.autoscroll.ReactiveAutoScroll;
import sneer.bricks.skin.windowboundssetter.WindowBoundsSetter;
import sneer.bricks.snapps.system.log.gui.LogConsole;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;

class LogConsoleImpl extends JFrame implements LogConsole {

	private static final String TITLE = "Log";
	private static final int CONSOLE_LINE_LIMIT = 1000;

	private final Synth _synth = my(Synth.class);
	{_synth.load(this.getClass());}
	
	private final Integer _OFFSET_X = 20;
	private final Integer _OFFSET_Y = 0;
	private final Integer _HEIGHT = 160;
	private final Integer _X = 10;
		
	private final MenuGroup<JPopupMenu> _popupMenu = my(MenuFactory.class).createPopupMenu();
	private final MainMenu _mainMenu = my(MainMenu.class);

	private final JTabbedPane _tab = new JTabbedPane();
	private final JTextArea _txtLog = new JTextArea();
	
	private final JScrollPane _autoScroll = autoScroll();
	
	@SuppressWarnings("unused")
	private WeakContract refToAvoidGc;
	
	LogConsoleImpl(){
		super();
		my(Dashboard.class);
		addMenuAction();
		my(GuiThread.class).invokeLater(new Closure(){ @Override public void run() {
			initGui();
//			initTranslucentWindow();
			initWindowListener();
		}});
		
		Signal<String> processTitle = my(ProcessTitle.class).title();
		refToAvoidGc = processTitle.addPulseReceiver(new Runnable() {	@Override	public void run() {
				updateTitle();
		}});
		
	}
	
	protected void initWindowListener() {
		this.addWindowListener(new WindowAdapter(){
			boolean isAutoScrollOn = false;
			BoundedRangeModel model = model();
			
			@Override public void windowDeiconified(WindowEvent e) { if(isAutoScrollOn) placeAtEnd(); }
			@Override public void windowIconified(WindowEvent e) { isAutoScrollOn = isAtEnd(); }
			
			private BoundedRangeModel model() { return _autoScroll.getVerticalScrollBar().getModel(); }	
			
			private boolean isAtEnd() {
				return model.getValue() + model.getExtent() == model.getMaximum(); }
			
			private void placeAtEnd() {
				model.setValue(model.getMaximum() - model.getExtent()); 
		}});
	}

//	private void initTranslucentWindow() {
//		TimingFramework timing = my(TimingFramework.class);
//		final Animator fade = timing.windowOpacity().animator(this, 1f, 0.2f, 1000, 200);
//		fade.playForward();
//		
//		this.addWindowFocusListener(new WindowFocusListener(){
//			@Override public void windowLostFocus(WindowEvent e) { 
//				fade.playForward(); 
//			}
//			@Override public void windowGainedFocus(WindowEvent e) { 
//				fade.playBackward();
//			}
//		});
//	}
	
	private void addMenuAction() {
		_mainMenu.addAction(40, "Open Log Console", new Closure() { @Override public void run() {
			open();
		}});
	}

	private void open() {
		setVisible(true);
	}

	private void initGui() {
		_txtLog.setEditable(false);
		getContentPane().setLayout(new BorderLayout());
		
		my(Synth.class).attach(_tab, "LogConsoleTab");
		
		_tab.addTab("", loadIcon("log.png"), _autoScroll, "Log");
		_tab.addTab("", loadIcon("filter.png"), initFilterGui(), "Filter");
		
		_tab.setTabPlacement(SwingConstants.RIGHT);
		_tab.addChangeListener(new ChangeListener(){ @Override public void stateChanged(ChangeEvent e) {
			updateTitle();
		}});
		
		getContentPane().add(_tab, BorderLayout.CENTER);
		
		initClearLogAction();
		
		final WindowBoundsSetter wbSetter = my(WindowBoundsSetter.class);
		wbSetter.runWhenBaseContainerIsReady(new Closure() { @Override public void run() {
			Rectangle unused = wbSetter.unusedArea();
			setBounds(_X , unused.height-_HEIGHT-_OFFSET_Y, unused.width-_OFFSET_X, _HEIGHT-_OFFSET_Y);
//			setFocusableWindowState(false);
//			setVisible(true);
//			setFocusableWindowState(true);
		}});
	}

	private ImageIcon loadIcon(String fileName) {
		return new ImageIcon(this.getClass().getResource(fileName));
	}

	private JPanel initFilterGui() {
		JPanel filter = new JPanel();
		_synth.attach(filter, "FilterPanel");
		filter.setLayout(new GridBagLayout());
		
		final ListRegister<String> whiteListEntries = my(LogFilter.class).whiteListEntries();
		final ListWidget<String> includes = my(ReactiveWidgetFactory.class).newList(whiteListEntries.output());
		JScrollPane scroll2 = new JScrollPane();
		scroll2.getViewport().add(includes.getComponent());
		scroll2.setBorder(new TitledBorder("Log Events That Contain:"));
		filter.add(scroll2, new GridBagConstraints(0,0,1,2,1.0,1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,2,0,0), 0,0));
		
		final JTextField newInclude = new JTextField();
		newInclude.setBorder(new TitledBorder(""));
		filter.add(newInclude, new GridBagConstraints(0,2,1,1,1.0,0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,4,2,2), 0,0));
		
		JButton addButton = new JButton();
		JButton delButton = new JButton();

		_synth.attach(addButton,"AddButton");
		_synth.attach(delButton,"DelButton");
		
		filter.add(delButton, new GridBagConstraints(1,0,1,1,0.0,0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0,0));
		filter.add(addButton, new GridBagConstraints(1,2,1,1,0.0,0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0,0));
		
		initAddFilterAction(whiteListEntries, newInclude, addButton);
		initDeleteFilterAction(whiteListEntries, includes, delButton);
		return filter;
	}

	private void initAddFilterAction( final ListRegister<String> whiteListEntries, final JTextField newInclude, JButton addButton) {
		addButton.addActionListener(new ActionListener(){ @Override public void actionPerformed(ActionEvent e) {
			String value = newInclude.getText();
			newInclude.setText("");
			if(value.length()==0) return;
			whiteListEntries.add(value);
		}});
	}

	private void initDeleteFilterAction(
			final ListRegister<String> whiteListEntries,
			final ListWidget<String> includes, JButton delButton) {
		delButton.addActionListener(new ActionListener(){ @Override public void actionPerformed(ActionEvent e) {
			Object[] values = includes.getMainWidget().getSelectedValues();
			for (Object value : values) 
				whiteListEntries.remove((String)value);
		}});
	}

	private void initClearLogAction() {
		
		_popupMenu.addAction(100, new Action(){
			@Override public String caption() { return "Clear Log";	}
			@Override public void run() { _txtLog.setText("");}
		});
		
		my(PopupTrigger.class).listen(_txtLog, new Consumer<MouseEvent>(){ @Override public void consume(MouseEvent e) {
			_popupMenu.getWidget().show(e.getComponent(),e.getX(),e.getY());
		}});
	}

	
	private JScrollPane autoScroll() {
		EventSource<String> loggedMessages = my(AssynchronousBuffers.class).createFor(my(LogNotifier.class).loggedMessages(), "LogConsole buffer");
		JScrollPane scroll = my(ReactiveAutoScroll.class).create(loggedMessages, new Consumer<String>() { @Override public void consume(String message) {
			if (_txtLog.getLineCount() > CONSOLE_LINE_LIMIT)
				_txtLog.setText(message);
			else
				_txtLog.append(message);
		}});
		scroll.getViewport().add(_txtLog);
		return scroll;
	}

	
	private void updateTitle() {
		setTitle(TITLE + " - " + my(ProcessTitle.class).title() + filter());
	}

	
	private String filter() {
		return _tab.getSelectedIndex()==0
			? ""
			: " (Filter)";
	}

}