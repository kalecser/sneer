package sneer.bricks.snapps.chat.gui.panels.impl;

import java.awt.Color;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import basis.lang.exceptions.NotImplementedYet;

import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.snapps.chat.ChatMessage;

class ShoutPainter {
	
	private final DefaultStyledDocument _document;
	
	private Style _space;
	private Style _time;
	private Style _shout;
	private Style _nick;

	ShoutPainter(DefaultStyledDocument styledDocument) {
		_document = styledDocument;
		Style def = StyleContext.getDefaultStyleContext().
        getStyle(StyleContext.DEFAULT_STYLE);

		_space = _document.addStyle("space", def);
		StyleConstants.setFontSize(_space, 4);

		_time = _document.addStyle("time", def);
		StyleConstants.setFontFamily(_time, "Verdana");
		StyleConstants.setFontSize(_time, 9);
		StyleConstants.setForeground(_time, Color.LIGHT_GRAY);

		_nick = _document.addStyle("nick", _time);
		StyleConstants.setForeground(_nick, Color.BLACK);
		StyleConstants.setFontSize(_nick, 10);
		StyleConstants.setBold(_nick, true);

		_shout = _document.addStyle("shout", def);
		StyleConstants.setFontFamily(_shout, "Verdana");
		StyleConstants.setFontSize(_shout, 12);		
		
		_document.addStyle("time", _time);
		_document.addStyle("shout", _shout);
	}

	void repaintAllShouts(ListSignal<ChatMessage> messages) {
		try {
			_document.remove(0, _document.getLength());
		} catch (BadLocationException e) {
			throw new NotImplementedYet(e); // Fix Handle this exception.
		}
		for (ChatMessage message : messages) 
			appendMessage(message);
	}
	
	void appendMessage(ChatMessage message) {
		try {
			_document.insertString(_document.getLength(), nick(message) ,  _nick);
			_document.insertString(_document.getLength(), header(message) ,  _time);
			_document.insertString(_document.getLength(), message.text ,  _shout);
			_document.insertString(_document.getLength(), "\n\n" ,  _space);
		} catch (BadLocationException e) {
			throw new NotImplementedYet(e); // Fix Handle this exception.
		}	
	}
	
	private String header(ChatMessage message){		
		return new StringBuilder().append(" - ")
			.append(ShoutUtils.getFormatedShoutTime(message)).append("\n").toString();
	}

	private String nick(ChatMessage shout) {
		if(ShoutUtils.isMyOwnShout(shout)) return "Me";
		
		return ShoutUtils.publisherNick(shout);
	}
}
