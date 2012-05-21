package sneer.bricks.snapps.chat.gui.panels.impl;

import java.awt.Color;
import java.awt.Image;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.snapps.chat.gui.panels.Message;
import basis.lang.exceptions.NotImplementedYet;

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

	void repaintAllShouts(ListSignal<Message> messages) {
		try {
			_document.remove(0, _document.getLength());
		} catch (BadLocationException e) {
			throw new NotImplementedYet(e); // Fix Handle this exception.
		}
		for (Message message : messages) 
			appendMessage(message);
	}
	
	void appendMessage(Message message) {
		try {
			_document.insertString(_document.getLength(), "avatar" , addStyleImage(message.avatar()));
			_document.insertString(_document.getLength(), " "+message.author() ,  _nick);
			_document.insertString(_document.getLength(), header(message) ,  _time);
			_document.insertString(_document.getLength(), message.text() ,  _shout);
			_document.insertString(_document.getLength(), "\n\n" ,  _space);
		} catch (BadLocationException e) {
			throw new NotImplementedYet(e); // Fix Handle this exception.
		}	
	}
	
	private String header(Message message){		
		return new StringBuilder().append(" - ")
			.append(getFormatedShoutTime(message)).append("\n").toString();
	}

	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
	
	static String getFormatedShoutTime(Message message) {
		return FORMAT.format(new Date(message.time()));
	}	
	
	public Style addStyleImage(Image image) {
			Style style = _document.addStyle("", null);
		 	StyleConstants.setIcon(style, new ImageIcon(image));
		return style;
	}
}

