package sneer.bricks.skin.widgets.reactive.impl;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.skin.widgets.reactive.NotificationPolicy;
import basis.lang.PickyConsumer;

class RTextPaneImpl extends RAbstractField<JTextPane> {
	
	private static final String LINE_BREAK_STRING = "\n\r";
	private static final long serialVersionUID = 1L;

	RTextPaneImpl(Signal<?> source, PickyConsumer<? super String> setter, NotificationPolicy notificationPolicy) {
		super(new JTextPane(), source, setter, notificationPolicy);
		_decorator = new ChangeInfoDecorator(_textComponent){ @Override void decorate(boolean notified) {
			//ignore, do nothing.
		}};
	}

	@Override
	protected void addDoneListenerCommiter() {
        _textComponent.addKeyListener(new KeyAdapter() { @Override public void keyPressed(KeyEvent e) {
    		if (isEnterKey(e)) processEnterKeyPress(e);
        }});
	}
	
	private void processEnterKeyPress(KeyEvent e) {
		if (hasModifier(e)){
			insertLineBreak();
			return;
		}
		commitTextChanges();
	}

	protected void insertLineBreak() {
	     try {
			int carretPosition = _textComponent.getCaretPosition();
			StyledDocument document = (StyledDocument) _textComponent.getDocument();
			SimpleAttributeSet attributes = new SimpleAttributeSet( document.getCharacterElement(carretPosition).getAttributes());
			attributes.addAttribute(LINE_BREAK_STRING, Boolean.TRUE);
			document.insertString(carretPosition, LINE_BREAK_STRING, attributes);
			_textComponent.setCaretPosition(carretPosition+1);
		} catch (BadLocationException e) {
			throw new basis.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
	}
	
	@Override
	public String getText() {
		return super.getText().trim();
	}

	private boolean hasModifier(KeyEvent e) {
		return e.isControlDown() || e.isAltDown() || e.isShiftDown();
	}

	private boolean isEnterKey(KeyEvent e) {
		return e.getKeyCode() == KeyEvent.VK_ENTER;
	}
}