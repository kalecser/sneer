package sneer.bricks.pulp.blinkinglights.impl;

import static basis.environments.Environments.my;
import sneer.bricks.hardware.gui.actions.Action;
import sneer.bricks.hardware.io.log.stacktrace.StackTraceLogger;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.collections.ListSignal;

class LightImpl implements Light {
	
	static final int NEVER = 0;
	
	Register<Boolean> _isOn = my(Signals.class).newRegister(false);

	private final LightType _type;
	
	final Register<String> _caption = my(Signals.class).newRegister(null);
	Throwable _error;
	String _helpMessage;

	final private ListRegister<Action> _actions = my(CollectionSignals.class).newListRegister();

	
	LightImpl(LightType type) {
		_type = type;
	}

	@Override public Throwable error() { return _error; }
	@Override public Signal<Boolean> isOn() { return _isOn.output(); }
	@Override public Signal<String> caption() { return _caption.output(); }
	@Override public LightType type() { return _type; }
	@Override public String helpMessage() { return _helpMessage; }
	
	void turnOff() {
		_isOn.setter().consume(false);
	}

	void turnOn() {
		_isOn.setter().consume(true);
	}

	@Override
	public ListSignal<Action> actions() {
		return _actions.output();
	}

	@Override
	public void addAction(Action action) {
		_actions.add(action);
	}
	
	@Override
	public String toString() {
		return "" + _type + ": " + _caption + (_error != null ? " - " + my(StackTraceLogger.class).stackTrace(_error) : "");
	}
}