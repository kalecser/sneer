package sneer.bricks.snapps.games.go.impl.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor;
import basis.environments.ProxyInEnvironment;
import basis.lang.Closure;

public class ActionsPanel extends JPanel implements NextToPlayListeter{
	
	private static final long serialVersionUID = 1L;
	private final StoneColor _side;
	
	private JButton passButton;
	private JButton resignButton;

	
	public ActionsPanel(final Closure pass, final Closure resign, StoneColor side, GoBoardPanel board) {
		_side = side;
		 
		passButton = new JButton("Pass");
		resignButton = new JButton("Resign");
		 
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
	
		board.addNextToPlayListener(this);
		
		setVisible(true);

	}


	@Override
	public void nextToPlay(StoneColor _nextToPlay) {
		boolean isMyTurn = _nextToPlay == _side;
		passButton.setEnabled(isMyTurn);
		resignButton.setEnabled(isMyTurn);
	}


}
