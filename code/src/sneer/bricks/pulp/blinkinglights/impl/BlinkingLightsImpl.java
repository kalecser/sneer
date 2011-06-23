package sneer.bricks.pulp.blinkinglights.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.HashSet;
import java.util.Set;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.foundation.lang.ByRef;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.exceptions.FriendlyException;
import sneer.foundation.util.concurrent.Latch;

class BlinkingLightsImpl implements BlinkingLights {
	
	private final Object _lock = new Object();
	
	private final ListRegister<Light> _lights = my(CollectionSignals.class).newListRegister();
	private final Set<WeakContract> _turnOffContracts = new HashSet<WeakContract>();
	
	@Override
	public Light turnOn(LightType type, String caption, String helpMessage, Throwable t, int timeout) {
		Light result = prepare(type);
		turnOnIfNecessary(result, caption, helpMessage, t, timeout);
		return result;
	}
	
	@Override
	public Light turnOn(LightType type, String caption, String helpMessage, Throwable t) {
		return turnOn(type, caption, helpMessage, t, LightImpl.NEVER);
	}

	@Override
	public Light turnOn(LightType type, String caption, String helpMessage, int timeToLive) {
		return turnOn(type, caption, helpMessage, null, timeToLive);
	}

	@Override
	public Light turnOn(LightType type, String caption, String helpMessage) {
		return turnOn(type, caption, helpMessage, LightImpl.NEVER);
	}
	
	@Override
	public ListSignal<Light> lights() {
		return _lights.output();
	}
	
	@Override
	public void turnOffIfNecessary(Light light) {
		synchronized(_lock) {
			if (!light.isOn().currentValue()) return;
			_lights.remove(light);
			((LightImpl)light).turnOff();
		}
	}
	
	private void turnOffIn(final Light light, int millisFromNow) {
		final Latch added = new Latch();
		final ByRef<WeakContract> weakContract = ByRef.newInstance();
		weakContract.value = my(Timer.class).wakeUpInAtLeast(millisFromNow, new Closure() { @Override public void run() {
			
			turnOffIfNecessary(light);
			
			added.waitTillOpen();
			_turnOffContracts.remove(weakContract.value);
		}});
		
		_turnOffContracts.add(weakContract.value);
		added.open();
	}

	@Override
	public Light prepare(LightType type) {
		return new LightImpl(type);
	}

	@Override
	public void turnOnIfNecessary(Light light, FriendlyException e) {
		turnOnIfNecessary(light, e, LightImpl.NEVER);
	}
	
	@Override
	public void turnOnIfNecessary(Light light, FriendlyException e, int timeout) {
		turnOnIfNecessary(light, e.getMessage(), e.getHelp(), e, timeout);
	}

	@Override
	public void turnOnIfNecessary(Light light, String caption, String helpMessage) {
		turnOnIfNecessary(light, caption, helpMessage, null, LightImpl.NEVER);
	}
	
	@Override
	public void turnOnIfNecessary(Light light, String caption, Throwable t) {
		turnOnIfNecessary(light, caption, null, t, LightImpl.NEVER);
	}

	@Override
	public void turnOnIfNecessary(Light light, String caption, String helpMessage, Throwable t) {
		turnOnIfNecessary(light, caption, helpMessage, t, LightImpl.NEVER);
	}
	
	@Override
	public void turnOnIfNecessary(Light light_, String caption, String helpMessage, Throwable t, int timeout) {
		synchronized(_lock) {
			LightImpl light = (LightImpl)light_;
	
			if (light.isOn().currentValue()) return;
			light.turnOn();
			light._caption = caption;
			light._error = t;
			light._helpMessage = helpMessage == null ? "If this problem doesn't go away on its own, get an expert sovereign friend to help you. ;)" : helpMessage;
			
			log(light_.type(), caption);
			
			_lights.add(light);
			
			if (timeout != LightImpl.NEVER)
				turnOffIn(light, timeout);
		}
	}

	private void log(LightType lightType, String caption) {
		my(Logger.class).log(severityTag(lightType), caption);
	}

	
	private String severityTag(LightType lightType) {
		if (lightType == LightType.ERROR) return "> > > > > > ERROR: ";
		if (lightType == LightType.WARNING) return "> > > WARNING: ";
		return "   ";
	}

}