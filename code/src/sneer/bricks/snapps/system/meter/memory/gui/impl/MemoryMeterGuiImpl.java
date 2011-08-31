package sneer.bricks.snapps.system.meter.memory.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import sneer.bricks.hardware.gui.guithread.GuiThread;
import sneer.bricks.hardware.ram.meter.MemoryMeter;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.main.dashboard.InstrumentPanel;
import sneer.bricks.skin.main.icons.Icons;
import sneer.bricks.skin.main.instrumentregistry.InstrumentRegistry;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.bricks.skin.widgets.reactive.TextWidget;
import sneer.bricks.snapps.system.meter.memory.gui.MemoryMeterGui;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Functor;

class MemoryMeterGuiImpl implements MemoryMeterGui {

	private final InstrumentRegistry _instruments = my(InstrumentRegistry.class);
	private final ReactiveWidgetFactory _factory = my(ReactiveWidgetFactory.class);
	private final MemoryMeter _meter = my(MemoryMeter.class);
	
	private final JLabel _gc = newGcButton(); //Fix the nimbus border button bug.
	private final JLabel _maxMemory = new JLabel();
	private final TextWidget<JLabel> _usedMemoryPeak = newLabel(_meter.usedMBsPeak(), "Peak: ");
	private final TextWidget<JLabel> _usedMemoryCurrent	= newLabel(_meter.usedMBs(), "MB Used: ");

	
	public MemoryMeterGuiImpl() {
		_instruments.registerInstrument(this);
	} 


	@Override
	public void init(InstrumentPanel window) {
		JComponent container = (JComponent) window.contentPane();
		container.setLayout(new FlowLayout(FlowLayout.CENTER));
		container.add(_usedMemoryCurrent.getComponent());
		container.add(_gc);
		container.add(_usedMemoryPeak.getComponent());

		_maxMemory.setText("(Max " + _meter.maxMBs() + ")");
		container.add(_maxMemory);
	}


	private JLabel newGcButton() {
		Icon icon = my(Icons.class).load(this.getClass(), "recycle.png");
		JLabel gcButton = new JLabel(icon);
		gcButton.setHorizontalAlignment(SwingConstants.CENTER);
		gcButton.setPreferredSize(new Dimension(20, 20));
		gcButton.addMouseListener(new MouseListener() { @Override public void mouseReleased(MouseEvent e) { }  @Override public void mousePressed(MouseEvent e) { }  @Override public void mouseExited(MouseEvent e) { }  @Override public void mouseEntered(MouseEvent e) { }  @Override public void mouseClicked(MouseEvent e) {
			System.gc();
		}});
		return gcButton;
	}

	
	private TextWidget<JLabel> newLabel(final Signal<Integer> source, final String prefix) {
		final Object ref[] = new Object[1];
		my(GuiThread.class).invokeAndWait(new Closure(){ @Override public void run() {//Fix Use GUI Nature
			ref[0] = _factory.newLabel(my(Signals.class).adapt(source, 	new Functor<Integer, String>(){@Override public String evaluate(Integer value) {
				return prefix + value;
			}}));
		}});
		return (TextWidget<JLabel>) ref[0];
	}
	

	@Override
	public int defaultHeight() {
		return DEFAULT_HEIGHT;
	}

	
	@Override
	public String title() {
		return "Memory Meter";
	}
}