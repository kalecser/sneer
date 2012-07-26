package sneer.bricks.snapps.system.blinkinglights.gui.impl;

import static basis.environments.Environments.my;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import sneer.bricks.hardware.gui.actions.Action;
import sneer.bricks.hardware.gui.guithread.GuiThread;
import sneer.bricks.hardware.gui.images.Images;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.main.dashboard.InstrumentPanel;
import sneer.bricks.skin.main.instrumentregistry.InstrumentRegistry;
import sneer.bricks.skin.widgets.reactive.LabelProvider;
import sneer.bricks.skin.widgets.reactive.ListWidget;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.bricks.snapps.system.blinkinglights.gui.BlinkingLightsGui;
import basis.lang.Closure;

class BlinkingLightsGuiImpl implements BlinkingLightsGui {
	
	private final InstrumentRegistry _instrumentManager = my(InstrumentRegistry.class);
	private final Map<LightType, Signal<Image>> _images = new HashMap<LightType, Signal<Image>>();

	private ListWidget<Light> _lightsList;
	private Container _container;
	
	BlinkingLightsGuiImpl(){
		loadImage("good_news.png", LightType.GOOD_NEWS);
		loadImage("info.png", LightType.INFO);
		loadImage("warn.png", LightType.WARNING);
		loadImage("error.png", LightType.ERROR);

		_instrumentManager.registerInstrument(this);
	}

	@Override public void init(InstrumentPanel window) {
		_container = window.contentPane();
		_lightsList = my(ReactiveWidgetFactory.class).newList(
				my(BlinkingLights.class).lights(), new BlinkingLightsLabelProvider());
		iniGui();
		new AlertWindowSupport();
	}

	@Override public int defaultHeight() {return 100;}
	@Override public String title() { return "Blinking Lights"; }	
	
	private final class BlinkingLightsLabelProvider implements LabelProvider<Light> {
		@Override public Signal<String> textFor(Light light) { return light.caption(); }
		@Override public Signal<Image> imageFor(Light light) { return _images.get(light.type()); }
	}

	private void iniGui() {
		//Optimize set the scroll panel size to same size of window to prevent a BL label crop.
		//			 label now:        "bla, bla, bla, bla, bla, bla, b"  (crop: "la")  
		//			 label after fix: "bla, bla, bla, bla, bla, bla..."
		JScrollPane scrollPane = new JScrollPane();

		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getViewport().add(_lightsList.getComponent());

		_container.setLayout(new BorderLayout());
		_container.add(scrollPane, BorderLayout.CENTER);
	}

	private void loadImage(String fileName, LightType type) {
		_images.put(type, 
			my(Signals.class).constant(
				my(Images.class).getImage(BlinkingLightsGuiImpl.class.getResource(fileName))
			));
	} 
	
	private final class AlertWindowSupport{
//		private static final int HORIZONTAL_LIMIT = 600;
		
		private static final String HELP = "HELP";
		private static final String STACK_TRACE = "STACK_TRACE";
		
		private JDialog _window;
		private JTextPane _textPane;
		private JScrollPane _scroll;
		private JPanel _actionsPanel;

		protected Light _light;
		
		private AlertWindowSupport(){
			initGui();
			initMouseListener();
		}

		private void initGui() {
			_window = new JDialog((JFrame)SwingUtilities.windowForComponent(_container), false);
			_window.setSize(300, 300);
			
			_textPane = new JTextPane();
			_textPane.setOpaque(false);
			_textPane.setEditable(false);
			initDocumentStyles(_textPane.getStyledDocument());
			
			_scroll = new JScrollPane();
			_scroll.getViewport().add(_textPane);
			_scroll.setOpaque(false);
			
			_actionsPanel = new JPanel();
			
			Container panel = _window.getContentPane();
			panel.setLayout(new BorderLayout());		
			panel.add(_scroll, BorderLayout.CENTER);
			panel.add(_actionsPanel, BorderLayout.SOUTH);
			_scroll.setBorder(new EmptyBorder(5,5,5,5));
		}

		private void initMouseListener() {
			_lightsList.getComponent().addMouseListener(new MouseAdapter(){ @Override public void mouseReleased(final MouseEvent event) {
				_light = getClickedLight(event);
				
				if(_light != null)	
					show(_light);
			}});
		}		

		private Light getClickedLight(final MouseEvent event) {
			JList<Light> list = (JList<Light>)event.getSource();
			list.setSelectedIndex(list.locationToIndex(event.getPoint()));
			return list.getSelectedValue();
		}
		
		private void show(final Light light){
			setWindowTitle(light);
			setWindowsMessage(light);
			addButtonsToActionsPanel(light);
			setWindowBounds();
			_window.setVisible(true);
			placeScrollAtTheBegining();
		}

		private void placeScrollAtTheBegining() {
			my(GuiThread.class).invokeLater(new Closure(){ @Override public void run() {
				scrollModel().setValue(scrollModel().getMinimum()-scrollModel().getExtent());
			}});
		}

		private BoundedRangeModel scrollModel() {
			return _scroll.getVerticalScrollBar().getModel();
		}

		private void setWindowTitle(Light light) {
			_window.setTitle(light.caption().currentValue());
			_window.setIconImage(_images.get(light.type()).currentValue());
		}
		
		private void setWindowsMessage(Light light) {
			_textPane.setText("");
			StyledDocument doc = _textPane.getStyledDocument();
			appendStyledText(doc, light.helpMessage(), HELP);
			
			if(light.error()==null) return;
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(out);
			light.error().printStackTrace(ps);
			String stack = new String(out.toByteArray());
			ps.close();
			
			appendStyledText(doc, "\n\n" + stack.trim(), STACK_TRACE);
		}
		
		private void addButtonsToActionsPanel(Light light) {
			_actionsPanel.removeAll();
			for (final Action action : light.actions()) {
				JButton button = new JButton(action.caption());
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						action.run();
						_window.setVisible(false);
					}
				});
				_actionsPanel.add(button);
			}
		}

		private void setWindowBounds() {
			_window.pack();
//			my(WindowBoundsSetter.class).setBestBounds(_window, _container, HORIZONTAL_LIMIT);
			_window.setLocationRelativeTo(_container);
		}
		
		private void appendStyledText(StyledDocument doc, String msg, String style) {
			try {
				doc.insertString(doc.getLength(), msg, doc.getStyle(style));
			} catch (BadLocationException e) {
				throw new basis.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
			}
		}

		private void initDocumentStyles(StyledDocument doc) {
			Style def = StyleContext.getDefaultStyleContext().getStyle( StyleContext.DEFAULT_STYLE );
		    
		    Style help = doc.addStyle( HELP, def );
		    StyleConstants.setBold(help, true);
		    doc.addStyle(HELP, help);
		    
		    Style stack = doc.addStyle( STACK_TRACE, def );
		    StyleConstants.setFontSize( stack, 11 );
		    doc.addStyle(STACK_TRACE, stack);
		}
	}
}