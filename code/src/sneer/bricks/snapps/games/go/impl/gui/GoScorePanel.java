package sneer.bricks.snapps.games.go.impl.gui;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class GoScorePanel extends JPanel{

	private static final long serialVersionUID = 1L;
	private JLabel _blackScore;
	private JLabel _whiteScore;

	public GoScorePanel(int scoreBlack, int scoreWhite) {		
		JSeparator space= new JSeparator(SwingConstants.VERTICAL);
		space.setPreferredSize(new Dimension(8,0));
		add(new JLabel("Score:"));
		add(space);
		add(new JLabel("Black"));
		_whiteScore = new JLabel(scoreWhite+"");
		_blackScore = new JLabel(scoreBlack+"");
		
		add(_blackScore);
		add(new JLabel("White"));
		add(_whiteScore);

		setVisible(true);
	}

	public void updateScore(int blackScore, int whiteScore) {
		_whiteScore.setText(""+whiteScore);
		_blackScore.setText(""+blackScore);
	}
}
