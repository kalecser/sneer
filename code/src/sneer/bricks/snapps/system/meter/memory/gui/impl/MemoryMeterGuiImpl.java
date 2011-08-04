package sneer.bricks.snapps.system.meter.memory.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

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
	
	private final JButton _gc = newGcButton();
	private final JLabel _maxMemory = new JLabel();
	private final TextWidget<JLabel> _usedMemoryPeak = newLabel(_meter.usedMBsPeak(), "Peak: ");
	private final TextWidget<JLabel> _usedMemoryCurrent	= newLabel(_meter.usedMBs(), "MB Used: ");

	
	public MemoryMeterGuiImpl() {
		_instruments.registerInstrument(this);
	} 


	@Override
	public void init(InstrumentPanel window) {
		JComponent container = (JComponent) window.contentPane();
		
		_maxMemory.setText("(Max " + _meter.maxMBs() + ")");
		container.setLayout(new FlowLayout(FlowLayout.CENTER));

		container.add(_usedMemoryCurrent.getComponent());
		container.add(_gc);
		container.add(_usedMemoryPeak.getComponent());
		container.add(_maxMemory);
	}


	private JButton newGcButton() {
		Icon icon = my(Icons.class).load(this.getClass(), "recycleOn.png");
		JButton gcButton = new JButton(icon);
		gcButton.setMargin(new Insets(0, 0, 0, 0));
		gcButton.setBorder(new EmptyBorder(0, 0, 0, 0));
		gcButton.addActionListener(new ActionListener(){ @Override public void actionPerformed(ActionEvent e) {
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