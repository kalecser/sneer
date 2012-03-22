package sneer.bricks.snapps.games.go.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import basis.environments.ProxyInEnvironment;
import basis.lang.Closure;
import basis.lang.Consumer;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.snapps.games.go.GoBoard.StoneColor;

public class ActionsPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private final StoneColor _side;
	
	@SuppressWarnings("unused")
	private final WeakContract _refToAvoidGc;

	
	public ActionsPanel(final Closure pass, final Closure resign, StoneColor side, Signal<StoneColor> nextToPlay) {
		_side = side;
		
		final JButton passButton= new JButton("Pass");
		
		final JButton resignButton= new JButton("Resign");
		 
		add(passButton);
		add(resignButton);
		
		ActionListener listener = new ActionListener() { @Override public void actionPerformed(ActionEvent arg0) {
			pass.run();
		}};
		passButton.addActionListener(ProxyInEnvironment.newInstance(listener));
		
		listener = new ActionListener() { @Override public void actionPerformed(ActionEvent arg0) {
			resign.run();
		}};
		resignButton.addActionListener(ProxyInEnvironment.newInstance(listener));
	
		_refToAvoidGc = nextToPlay.addReceiver(new Consumer<StoneColor>() { @Override public void consume(StoneColor nextColor) {
			boolean isMyTurn = nextColor == _side;
			passButton.setEnabled(isMyTurn);
			resignButton.setEnabled(isMyTurn);
		}});
		
		setVisible(true);

	}


}
