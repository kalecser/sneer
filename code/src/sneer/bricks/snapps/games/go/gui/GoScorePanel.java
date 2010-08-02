package sneer.bricks.snapps.games.go.gui;

import static sneer.foundation.environments.Environments.my;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.bricks.skin.widgets.reactive.TextWidget;
import sneer.foundation.lang.Functor;

public class GoScorePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public GoScorePanel(Signal<Integer> scoreBlack, Signal<Integer> scoreWhite) {
		ReactiveWidgetFactory rfactory = my(ReactiveWidgetFactory.class);

		TextWidget<?> newLabelBlack = rfactory.newLabel(adaptToString(scoreBlack));
		TextWidget<?> newLabelWhite = rfactory.newLabel(adaptToString(scoreWhite));
		
		JSeparator space= new JSeparator(SwingConstants.VERTICAL);
		space.setPreferredSize(new Dimension(8,0));
		add(new JLabel("Score:"));
		add(space);
		add(new JLabel("Black"));
		add(newLabelBlack.getComponent());
		add(new JLabel("White"));
		add(newLabelWhite.getComponent());

		setVisible(true);
	}

	private Signal<String> adaptToString(Signal<Integer> input) {
		return my(Signals.class).adapt(input, new Functor<Integer, String>(){ @Override public String evaluate(Integer value) {
			return "" + value;
		}});
	}
}
