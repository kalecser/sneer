package sneer.bricks.software.bricks.repl.gui.impl;

import static basis.environments.Environments.my;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.software.bricks.repl.Repl;
import sneer.bricks.software.bricks.repl.ReplLang;
import sneer.bricks.software.bricks.repl.gui.ReplWindow;
import basis.lang.Closure;

public class ReplWindowImpl extends JFrame implements ReplWindow {

	private final JLabel _title = new JLabel("Repl");
	private final JTextArea textArea = new JTextArea();

	{
		addReplWindowToMenu();
	}
	
	public ReplWindowImpl() {
		
		setLayout(new BorderLayout());
		add(_title , BorderLayout.NORTH);
	
		super.add(textArea);
		super.setSize(300, 300);
		
		textArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
					executeAndShowResult();
				}
			}
			
		});
	}
	
	private void addReplWindowToMenu() {
		my(MainMenu.class).menu().addAction(90, "Repl", new Closure() { @Override public void run() {
			my(ReplWindow.class).open();
		}});
	}

	@Override
	public void open() {
		setVisible(true);
	}

	private void executeAndShowResult() {
		String selectedText = textArea.getSelectedText();
		if (selectedText != null && !selectedText.isEmpty()) {
			Object result = my(Repl.class).newEvaluatorFor(ReplLang.groovy).eval(selectedText);
			String resultString = "\n --> " + (result == null ? "null" : result.toString());
			textArea.append(resultString);
		}
	}
	
}
