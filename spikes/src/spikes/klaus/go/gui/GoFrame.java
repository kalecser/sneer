package spikes.klaus.go.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import sneer.bricks.pulp.reactive.Register;
import sneer.foundation.lang.Closure;
import spikes.klaus.go.Move;
import spikes.klaus.go.GoBoard.StoneColor;

public class GoFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private final StoneColor _side;
	
	public GoFrame(Register<Move> _move, StoneColor side, int horizontalPosition) {
		_side = side;
	
		setTitle("Go - " + _side.name());	  
	    setResizable(true);
	    setBounds(horizontalPosition, 0, 500, 575);
	    setVisible(true);
	    setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		addComponentPanel(_move);	
	}

	private void addComponentPanel(Register<Move> move) {
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		final GoBoardPanel goBoardPanel = new GoBoardPanel(move, _side);
		contentPane.add(goBoardPanel, BorderLayout.CENTER);
		
		JPanel goEastPanel = new JPanel();
		
		goEastPanel.setLayout(new FlowLayout());
		goEastPanel.add(new GoScorePanel(goBoardPanel.scoreBlack(), goBoardPanel.scoreWhite()));
		
		JSeparator space= new JSeparator(SwingConstants.VERTICAL);
		space.setPreferredSize(new Dimension(30,0));
		
		goEastPanel.add(space);
		Closure pass = new Closure() { @Override public void run() {
			goBoardPanel.passTurn();
		}};
		Closure resign = new Closure() { @Override public void run() {
			goBoardPanel.resignTurn();
		}};
		goEastPanel.add(new ActionsPanel(pass,resign, _side, goBoardPanel.nextToPlaySignal()));
				
		contentPane.add(goEastPanel, BorderLayout.SOUTH);
	}

}
