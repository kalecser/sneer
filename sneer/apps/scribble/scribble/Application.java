package scribble;

import java.util.List;

import sneer.kernel.api.SovereignApplicationAbstractImpl;
import sneer.kernel.gui.contacts.ContactAction;

public class Application extends SovereignApplicationAbstractImpl{

	private Scribble _delegate;

	@Override
	public String defaultName() {
		return "Scribble";
	}

	@Override
	public int trafficPriority() {
		return 2;
	}

	@Override
	public void start() {
		_delegate = new Scribble(_config);
	}

	@Override
	public List<ContactAction> contactActions() {
		return _delegate.contactActions();
	}
	
	@Override
	public Object initialState() {
		return null;
	}
	

}
