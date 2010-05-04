package sneer.bricks.snapps.system.meter.memory.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

import sneer.bricks.hardware.gui.guithread.GuiThread;
import sneer.bricks.hardware.ram.meter.MemoryMeter;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.main.dashboard.InstrumentPanel;
import sneer.bricks.skin.main.instrumentregistry.InstrumentRegistry;
import sneer.bricks.skin.main.synth.Synth;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.bricks.skin.widgets.reactive.TextWidget;
import sneer.bricks.snapps.system.meter.memory.gui.MemoryMeterGui;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Functor;

class MemoryMeterGuiImpl implements MemoryMeterGui {

	private final InstrumentRegistry _instruments = my(InstrumentRegistry.class);
	private final ReactiveWidgetFactory _factory = my(ReactiveWidgetFactory.class);
	private final MemoryMeter _meter = my(MemoryMeter.class);
	private final JButton _gc = new JButton();
	private final Synth _synth = my(Synth.class);
	
	private final JLabel _maxMemory = new JLabel();
	private final TextWidget<JLabel> _usedMemoryPeak = newLabel(_meter.usedMBsPeak(), "Peak: ", "MaxUsedMemoryLabel");
	private final TextWidget<JLabel> _usedMemoryCurrent
	= newLabel(_meter.usedMBs(), "MB Used: ", "CurrentMemoryLabel");

	public MemoryMeterGuiImpl() {
		_instruments.registerInstrument(this);
		initSynth();
	} 

	private void initSynth() {
		_synth.load(this.getClass());
		_synth.attach(_gc, "GCButton");
		_synth.attach(_maxMemory, "TotalMemoryLabel");
	}
	
	@Override
	public void init(InstrumentPanel window) {
		JComponent container = (JComponent) window.contentPane();
		
		_maxMemory.setText("(Max " + _meter.maxMBs() + ")");
		container.setLayout(new FlowLayout(FlowLayout.CENTER));

		_gc.addActionListener(new ActionListener(){ @Override public void actionPerformed(ActionEvent e) {
			System.gc();
		}});
		
		container.add(_usedMemoryCurrent.getComponent());
		container.add(_gc);
		container.add(_usedMemoryPeak.getComponent());
		container.add(_maxMemory);
	}
	
	private TextWidget<JLabel> newLabel(final Signal<Integer> source, final String prefix, final String synthName) {
		final Object ref[] = new Object[1];
		my(GuiThread.class).invokeAndWait(new Closure(){ @Override public void run() {//Fix Use GUI Nature
			ref[0] = _factory.newLabel(my(Signals.class).adapt(source, 	new Functor<Integer, String>(){@Override public String evaluate(Integer value) {
				return prefix + value;
			}}), synthName);
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