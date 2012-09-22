package sneer.bricks.software.bricks.repl.gui.impl;

import static basis.environments.Environments.my;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.software.bricks.repl.Evaluator;
import sneer.bricks.software.bricks.repl.Repl;
import sneer.bricks.software.bricks.repl.ReplConsole;
import sneer.bricks.software.bricks.repl.ReplLang;
import sneer.bricks.software.bricks.repl.gui.ReplWindow;
import basis.lang.Closure;

public class ReplWindowImpl extends JFrame implements ReplWindow {

	private final JTextArea textArea = new JTextArea();
	private final Repl repl = my(Repl.class);
	private final Evaluator evaluator = repl.newEvaluatorFor(ReplLang.groovy);
	private final ReplConsole console = repl.newConsoleFor(evaluator);

	{
		addReplWindowToMenu();
	}
	
	public ReplWindowImpl() {
		
		setLayout(new BorderLayout());
		JPanel pnlBtn = new JPanel();
		
		setTitle("Repl");
		
		addExecuteButton(pnlBtn);
		addResetButton(pnlBtn);
		
		add(textArea, BorderLayout.CENTER);
		add(pnlBtn, BorderLayout.SOUTH);
		setSize(300, 300);
		
		addKeyListeners();
	}

	private void addResetButton(JPanel pnlBtn) {
		JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) {
			resetConsole();
		}});
		pnlBtn.add(resetButton);
	}

	private void addExecuteButton(JPanel pnlBtn) {
		JButton executeButton = new JButton("Execute");
		executeButton.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) {
			executeAndShowResult();
		}});
		pnlBtn.add(executeButton);
	}

	private void addKeyListeners() {
		textArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.isControlDown()) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER)
						executeAndShowResult();
					else if(e.getKeyCode() == KeyEvent.VK_R)
						resetConsole();
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
		String code = textArea.getSelectedText();
		if(code == null || code.isEmpty())
			code = textArea.getText();
		if (code != null && !code.isEmpty()) {
			textArea.append(console.eval(code));
		}
	}
	
	private void resetConsole() {
		evaluator.reset();
		textArea.setText("");
	}
	
}
