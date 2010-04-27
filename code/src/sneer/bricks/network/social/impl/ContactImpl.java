package sneer.bricks.network.social.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.io.prevalence.map.ExportMap;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.PickyConsumer;
import sneer.foundation.lang.exceptions.Refusal;

class ContactImpl implements Contact {

	private final Register<String> _nickname;
	final PickyConsumer<String> _nicknameSetter;

	
	public ContactImpl(String nickname, final PickyConsumer<String> nicknameChecker) {
		_nickname = my(Signals.class).newRegister(nickname);
		_nicknameSetter = new PickyConsumer<String>() { @Override public void consume(String newNickname) throws Refusal {
			nicknameChecker.consume(newNickname);
			_nickname.setter().consume(newNickname);
		}};
		
		my(ExportMap.class).register(this);
		my(ExportMap.class).register(_nicknameSetter);
	}

	
	@Override
	public Signal<String> nickname() {
		return _nickname.output();
	}

	
	@Override
	public String toString() {
		return _nickname.output().currentValue();
	}
	
}