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

class MessagePainter {
	
	private final DefaultStyledDocument _document;
	
	private Style _space;
	private Style _time;
	private Style _text;
	private Style _nick;

	
	MessagePainter(DefaultStyledDocument styledDocument) {
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

		_text = _document.addStyle("message", def);
		StyleConstants.setFontFamily(_text, "Verdana");
		StyleConstants.setFontSize(_text, 12);		
		
		_document.addStyle("time", _time);
		_document.addStyle("message", _text);
	}

	
	void repaintAll(ListSignal<Message> messages) {
		try {
			_document.remove(0, _document.getLength());
		} catch (BadLocationException e) {
			throw new IllegalStateException(e);
		}
		for (Message message : messages) 
			append(message);
	}
	
	
	void append(Message message) {
		try {
			Image avatar = message.avatar();
			if (avatar != null)
				_document.insertString(_document.getLength(), "avatar" , addStyleImage(avatar));
			_document.insertString(_document.getLength(), " "+message.author() ,  _nick);
			_document.insertString(_document.getLength(), header(message) ,  _time);
			_document.insertString(_document.getLength(), message.text() ,  _text);
			_document.insertString(_document.getLength(), "\n\n" ,  _space);
		} catch (BadLocationException e) {
			throw new NotImplementedYet(e); // Fix Handle this exception.
		}	
	}
	
	private String header(Message message){		
		return new StringBuilder().append(" - ")
			.append(getFormatedTime(message)).append("\n").toString();
	}

	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
	
	static String getFormatedTime(Message message) {
		return FORMAT.format(new Date(message.time()));
	}	
	
	private Style addStyleImage(Image image) {
		Style style = _document.addStyle("", null);
		StyleConstants.setIcon(style, new ImageIcon(image));
		return style;
	}
}

