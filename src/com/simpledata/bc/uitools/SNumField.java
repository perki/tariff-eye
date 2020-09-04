/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 3 mars 2004
 * $Id: SNumField.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.uitools;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.apache.log4j.Logger;

import com.simpledata.bc.uicomponents.tools.JTextFieldBC;

/**
 *  Creates a textField which will format numbers.
 */
public abstract class SNumField extends JTextFieldBC {
	// TODO replace KeyAdapter by a Document to analyze entered data...
	private static final Logger m_log = Logger.getLogger(SNumField.class);
	
	private static char mark = '\'';
		
	/** Fields used for Regex*/
	private static Pattern withoutComma = Pattern.compile("\\-?[0-9]*");
	private static Pattern withComma = Pattern.compile("\\-?[0-9]+\\.[0-9]*");
	private static Pattern withPoint = Pattern.compile("\\-?[0-9]+,[0-9]*");
	private static Matcher matchWithoutComma = withoutComma.matcher("");
	private static Matcher matchWithComma = withComma.matcher("");
	private static Matcher matchWithPoint = withPoint.matcher("");
	
	
	/** number of digit after the coma, -1 if undef **/
	private int digitAfterComa = -1;
	
	/** 
	 * trucate or not the number when number of digit after coma is max,
	 * has no effect if digitAfterComa = -1
	 *  **/
	private boolean trucateAfterMaxDigit = false;
	
	/** 
	 * Old content of the cell, before editing. We can
	 * retrieve it striking escape
	 */
	protected final String oldContent;
	
	/**
	 * Construct an empty SNumField for decimal values, 
	 * using the default behaviour, i.e. when you
	 * click on the field the content is automaticly selected, since you hit the
	 * F2 key. It also recover the old value whe you hit ESC while editing. 
	 * 
	 */
	public SNumField() {
		this("");
	}
	
	/**
	 * Construct an SNumField, using the default behaviour, i.e. when you
	 * click on the field the content is automaticly selected, since you hit the
	 * F2 key. It also recover the old value whe you hit ESC while editing.
	 * @param d The value shown in the SNumField.
	 */
	public SNumField(double d) {
		this(formatNumber(d));
	}
	
	/** set the number of digits after the coma : -1 for undef **/
	public void setDigitAfterComa(int num) {
		digitAfterComa = num;
	}
	
	/** set trucate or not the number when number of digit after coma is max,
	 * has no effect if digitAfterComa = -1
	 *  **/
	public void setTruncateAfterMax(boolean b) {
		trucateAfterMaxDigit = b;
	}
	
	/**
	 * Construct an SNumField, using the default behaviour, i.e. when you
	 * click on the field the content is automaticly selected, since you hit the
	 * F2 key. It also recover the old value whe you hit ESC while editing.
	 * The SNumField shows numbers using decimals.
	 * @param content The String shown in the SNumField.
	 */
	public SNumField(String content) {
		this(content, false, true);
	}
	
	/**
	 * Construct an SNumField, for decimal numbers.
	 * @param content The orignal content of the field
	 * @param selectOnStartEdit If set to true, the text is automatically 
	 * selected when you click on the field.
	 */
	public SNumField(String content, boolean selectOnStartEdit) {
		this(content, false, selectOnStartEdit);
	}
	
	public SNumField(String content, boolean isInt, boolean selectOnStartEdit){
		this (content, isInt, selectOnStartEdit, true);
	}
	
	/**
	 * Construct an SNumField.
	 * @param content  Original content of the field.
	 * @param isInt    If set to true, the field accepts only integers.
	 * @param selectOnStartEdit If set to true, the text is automatically 
	 * selected when you click on the field.
	 * @param If set to true, disallow negative values.
	 */
	public SNumField(String content, boolean isInt, boolean selectOnStartEdit, 
			         boolean positive) {
		super(selectOnStartEdit);
		
		setAlignmentX(Component.LEFT_ALIGNMENT);
		this.addKeyListener(new SNumFieldKeyAdapter());
		
		SNumFieldDocument docu;
		if (isInt) {
			docu = new SNumFieldDocument(this, 
			        SNumFieldDocument.INTEGER, true, positive);
		} else {
			docu = new SNumFieldDocument(this, 
			        SNumFieldDocument.FLOAT, true, positive);
		}
		this.setDocument(docu);
		this.setText(content);
		this.oldContent = content;
		// select the text. Excel behaviour.
		this.selectAll();
		
	}

	
	/**
	 * Overriding parent's method
	 */
	public void setText(String t) {
		String s = SNumField.formatNumber(
					t,digitAfterComa,trucateAfterMaxDigit);
		//this.currentString = s;
		super.setText(s);
	}
	
	
	
	/**
	 * set a double value
	 */
	public void setDouble(double d) {		
		super.setText(formatNumber(d,digitAfterComa,trucateAfterMaxDigit));
	}
	
	public String getDisplayString() {
		return "";
	}
	
	/**
	 * return true if this double is too big to be displayed
	 * by double and need to be converted with BigDecimal
	 */
	public static boolean isBigDecimal(double d) {
		return (d > 7000000);
	}
	
	
	/**
	 */
	public static String formatNumber(double d) {
		if (isBigDecimal(d)) {
			return formatNumber(new BigDecimal(d).toString());
		}
		return formatNumber(""+d);
	}
	
	

	/**
	 * Format a number string into a readable format<br>
	 * 123 --> 123<br>
	 * 1234 --> 1'234<br>
	 * 23,4 --> 23.4<br>
	 * @param t the number
	 * @return
	 */
	public static String formatNumber(String t) {
		return formatNumber(t,-1,false);
	}
	
	
	/**
	 * @param t string to format
	 * @param numAfterComma number of digits displayed after the comma,
	 * if truncated false it is a minimum number
	 * @param truncate if true will truncate at numAfterComma
	 * @return
	 */
	public static String formatNumber(String t,  
			int numAfterComma, 
			boolean truncate) {
		String res = "";
		if (!((t == null) || (t.trim().equals("")))) {
			t = normalize(t);
			if (isValidNumber(t)) {
				res = addMarks(t);
			}
			if (numAfterComma >= 0) {
				res = formatTail(res, numAfterComma);
				if (truncate) {
					res = truncate(res, numAfterComma);
				}
			}
		}
		return res;
	}
	
	/**
	 */
	public static String formatNumber
	(double d,  int numAfterComma, boolean truncate) {
		double z = Math.pow(10,numAfterComma);
		d = Math.round(d*z) / z;
		return formatNumber(formatNumber(d),numAfterComma,truncate);
	}
	
	protected static boolean isValidNumber(String s) {
		matchWithoutComma.reset(s);
		matchWithComma.reset(s);
		matchWithPoint.reset(s);
		return ((matchWithComma.matches()) || 
				(matchWithoutComma.matches()) || 
				(matchWithPoint.matches()) );
	}
	
	protected static String normalize(String s) {
		s = s.replaceAll("'", "");
		return s.replaceAll(",", ".");
	}
	
	
	
	/**
	 * Format a normalized string representing a number<br>
	 * 123456789.098 becomes 123'456'789.098
	 * @param s
	 * @return
	 */
	protected static String addMarks(String s) {
		int posOfPoint = s.indexOf(".");
		String head = s;
		String tail = "";
		String sign = "";
		if (posOfPoint > -1) {
			head = s.substring(0,posOfPoint);
			tail = s.substring(posOfPoint);
		}
		boolean negative = (s.indexOf("-") > -1);
		if (negative) {
			sign = "-";
			head = head.substring(1);
		}
		
		char[] expHead = head.toCharArray();
		int numberOfChars = expHead.length + ((expHead.length-1) / 3 );
		char[] res = new char[numberOfChars];
		// We place the readPointer at the end of expHead
		int expHeadReadPointer = expHead.length - 1;
		int markCounter = 1;
		int resWritePointer = res.length - 1;
		while (resWritePointer > -1) {
			if (markCounter > 3 ) {
				// We must insert a mark
				markCounter = 0;
				res[resWritePointer] = mark;
			} else {
				// We read next char from expHead
				res[resWritePointer] = expHead[expHeadReadPointer];
				expHeadReadPointer--;
			}
			markCounter++;
			resWritePointer--;
		}
		
		head = new String(res);
		
		return (sign+head+tail);
	}
	
	/**
	 * Must be called on a normalized String
	 * @param s
	 * @param cursorPos
	 * @return an array of object : 1st the corrected String , 2nd the new cursor position
	 */
	protected static Object[] addMarks(String s, int cursorPos) {
		Object[] res = new Object[2];
		
		boolean negative = (s.indexOf("-") > -1);
		
		int pointPos = s.indexOf('.');
		if (pointPos == -1)
			pointPos = s.indexOf(',');
		int startIndex = s.length() - 1;
		if (pointPos > -1) {
			startIndex = pointPos-1;
		}
		StringBuffer buf = new StringBuffer(s);
		int newCursorPos = cursorPos;
		int digitsEncountered = 0;
		for (int i = startIndex; i> (negative ? 1 : 0); i--) {
			digitsEncountered++;
			if (digitsEncountered == 3) {
				// We add a mark
				buf.insert(i, '\'');
				if (newCursorPos >= i-1) {
					newCursorPos++;
				}
				digitsEncountered = 0;
			}
		}
		
		res[0] = buf.toString();
		res[1] = new Integer(newCursorPos);
		return res;
	}
	
	/**
	 * @param s
	 * @param cursorPos
	 * @return an array of object : 1st the corrected String , 2nd the new cursor position
	 */
	protected static Object[] normalize(String s, int cursorPos) {
		Object[] res = new Object[2];
		
		// We transform , in . and we examine how many occurences do we have
		s.replaceAll(",", ".");

		int startIndex = s.length()-1;
		StringBuffer buf = new StringBuffer(s);
		int newCursorPos = cursorPos;
		for (int i = startIndex; i>= 0; i--) {
			char c = buf.charAt(i);
			if (c == '\'') {
				// We must remove it
				buf.deleteCharAt(i);
				if ((cursorPos >= i) && (cursorPos > 0)) {
					newCursorPos--;
				}
			}
		}

		res[0] = buf.toString();
		res[1] = new Integer(newCursorPos);
		return res;
	}
	
	protected static String truncate(String s, int afterComma) {
		if (afterComma >= 0) {
			int posOfPoint = s.indexOf(".");
			if (afterComma == 0) posOfPoint--;
			if (posOfPoint > -1) {
				
				s = s.substring(0, posOfPoint + afterComma + 1);
			}
		}
		
		return s;
	}
	
	/**
	 * Format the number so that there are at least minAfterComma digits after the point
	 * @param s String to be formated
	 * @param minAfterComma minimal number of digits after comma, if -1 no min value
	 * @return
	 */
	protected static String formatTail(String s, int minAfterComma) {
		if (minAfterComma > 0) {
			int zerosToAdd = 0;
			int posOfPoint = s.indexOf(".");
			if (posOfPoint > -1) {
				String tail = s.substring(posOfPoint + 1);
				int dif = minAfterComma - tail.length();
				if (dif > 0) zerosToAdd = dif;
			} else {
				zerosToAdd = minAfterComma;
				if (posOfPoint == -1) s += ".";
			}
			while (zerosToAdd > 0) {
				s+= "0";
				zerosToAdd--;
			}
		}
		return s;
	}
	
	/**
	 * To get the number associated with this field
	 * @return
	 */
	public String getNonFormatedText() {
		return normalize(this.getText());
	}
	
	/**
	 * To get the number associated as a Double
	 * @return
	 */
	public Double getDouble() {
		Double res = null;
		try {
			String t = this.getNonFormatedText();
			res = new Double(new BigDecimal(t).doubleValue());
		} catch (NumberFormatException e) {

		}
		return res;
	}
	
	/**
	 * To get the number associated as an Integer
	 * @return
	 */
	public Integer getInteger() {
		Integer res = null;
		try {
			String t = this.getNonFormatedText();
			res = new Integer(new BigInteger(t).intValue());
		} catch (NumberFormatException e) {

		}
		return res;
	}
}

class SNumFieldKeyAdapter extends KeyAdapter {
	private final static Logger m_log = Logger.getLogger(SNumFieldKeyAdapter.class);
	
	public void keyReleased(KeyEvent evt) {
		if ((evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) || 
				(evt.getKeyCode() == KeyEvent.VK_DELETE)) {
			// We are facing a delete case
			try {
				((SNumField)evt.getSource()
						).getDocument().insertString(0, "", null);
			} catch (BadLocationException e) {
				m_log.error("Snumfield bad delete", e);
			}
		}
		
	}
	
	public void keyPressed(KeyEvent evt) {
		// Recover old value on escape
		if ((evt.getKeyCode() == KeyEvent.VK_ESCAPE)) {
			m_log.debug("Escape catched");
			SNumField field = (SNumField)evt.getSource();
			field.setText(field.oldContent);
			field.stopEditing(); // XXX ???
		}
	}
 	
}



/**
 * Document definig rules for the text entered and displayed
 */
class SNumFieldDocument extends PlainDocument {
	
	public final static int INTEGER = 0;
	public final static int FLOAT = 1;
	
	/** When we strike this key, the document adds 3 zeros */
	public final static char KILO = 'k';
	/** When we strike this key, the document adds 6 zeros */
	public final static char MEGA = 'm';
	
	private final static Logger m_log = Logger.getLogger(SNumFieldDocument.class);
	
	private int type = SNumFieldDocument.FLOAT;
	private SNumField owner = null;
	private boolean beepOnError = false;
	
	private final boolean m_positive;
	
	/** Fields used to allow roolback on bad text entry */
	private String oldValidNumber = "";
	private int oldValidCaretPos = 0;
	
	/**
	 * Create a new document
	 * @param ow SNumField to which it is attached
	 * @param type determines if it is FLOAT or INTEGER
	 */
	public SNumFieldDocument(SNumField ow, int type) {
		this(ow, type, true);
	}
	
	/**
	 * Create a new document
	 * @param type determines if it is FLOAT or INTEGER
	 * @param beep if true , the sytem will beep on type errors
	 */
	public SNumFieldDocument(SNumField ow, int type, boolean beep) {
		this (ow, type, beep, true);
	}
	
	public SNumFieldDocument(SNumField ow, int type, boolean beep, boolean positive){
		super();
		this.owner = ow;
		this.beepOnError = beep;
		this.m_positive = positive;
		if (type == SNumFieldDocument.INTEGER) {
			this.type = type;
		}
	}
	
	public void insertString
	(int offs, String str, AttributeSet as) throws BadLocationException {
		StringBuffer buf = new StringBuffer(str);
		int size = buf.length();
		char c;
		for (int i=0;i<size;i++) {
			c = buf.charAt(i);
			// SHORTCUTS for thousands and millions
			if (c == KILO) { 
				buf.deleteCharAt(i);
				buf.append("000");
			} else if (c == MEGA) {
				buf.deleteCharAt(i);
				buf.append("000000");
			}
			else if (!isAcceptedChar(c)) {
				if (this.beepOnError) {
					Toolkit.getDefaultToolkit().beep();
				}
				buf.deleteCharAt(i);
				i--;
				size--;
			}
		}
		try {
		super.insertString(offs, buf.toString(), as);
		} catch (BadLocationException e) {
			m_log.error("Bad location in SNumField Document", e);
		}

		String newText = this.owner.getText();
		int newCursorPos = this.owner.getCaretPosition();
		super.remove(0, super.getLength());
		Object[] results;
		results = SNumField.normalize(newText, newCursorPos);
		String resString = (String)results[0];
		int resInt = ((Integer)results[1]).intValue();
		if (!SNumField.isValidNumber(resString)) {
			// RollBack
			super.insertString(0, this.oldValidNumber, null);
			this.owner.setCaretPosition(this.oldValidCaretPos);
			return;
		}
		results = SNumField.addMarks(resString, resInt);
		this.oldValidNumber = (String)results[0];
		this.oldValidCaretPos = ((Integer)results[1]).intValue();
		
		super.insertString(0, this.oldValidNumber ,null);
		try {
		this.owner.setCaretPosition(this.oldValidCaretPos);
		} catch (Exception e) {
			m_log.error("------------- ERROR --------------");
		}
		
	}
	
	private boolean isAcceptedChar(char c) {
		boolean res;
		res = ((Character.isDigit(c)) || (c == '\''));
		if (this.type == SNumFieldDocument.FLOAT) {
			res = (res || (c == '.') || (c == ','));
		}
		if ( ! m_positive)
			res = res || (c=='-' && owner.getCaretPosition() == 0);
		return res;
	}
}

/*
 * $Log: SNumField.java,v $
 * Revision 1.2  2007/04/02 17:04:26  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:41  perki
 * First commit on sourceforge
 *
 * Revision 1.29  2004/09/23 06:27:56  perki
 * LOt of cleaning with the Logger
 *
 * Revision 1.28  2004/09/15 11:04:22  jvaucher
 * Added a textual message for the event code (method eventName())
 * Added the modification dialog box. But some changes are still not observable. See tickets for details
 *
 * Revision 1.27  2004/09/14 07:48:25  perki
 * Futures
 *
 * Revision 1.26  2004/09/13 15:28:44  jvaucher
 * Added some dictionaries.
 *
 * Revision 1.25  2004/09/10 16:29:48  jvaucher
 * Allows negative percentage for discount
 *
 * Revision 1.24  2004/09/10 13:03:09  jvaucher
 * SNumField stronger and stronger
 *
 * Revision 1.23  2004/09/09 12:43:08  perki
 * Cleaning
 *
 * Revision 1.22  2004/09/07 16:21:03  jvaucher
 * - Implemented the DispatcherBounds to resolve the feature request #24
 * The calculus on this dispatcher is not yet implemented
 * - Review the feature of auto select at startup for th SNumField
 *
 * Revision 1.21  2004/09/03 13:21:05  jvaucher
 * Fixed a SNumField edition problem
 *
 * Revision 1.20  2004/09/03 11:31:48  jvaucher
 * - Fixed ticket #2. coma problem in SNumField
 * - Fixed ticket #32, single digit input in SNumField
 *
 * Revision 1.19  2004/09/02 16:05:51  jvaucher
 * - Ticket #1 (JTextField behaviour) resolved
 * - Deadlock at loading problem resolved
 * - New kilo and million feature for the SNumField
 *
 * Revision 1.18  2004/08/01 18:00:59  perki
 * *** empty log message ***
 *
 * Revision 1.17  2004/07/31 12:01:00  perki
 * Still have problems with the progressbar
 *
 * Revision 1.16  2004/07/29 11:38:13  perki
 * Sliders should be ok now
 *
 * Revision 1.15  2004/07/26 17:39:37  perki
 * Filler is now home
 *
 * Revision 1.14  2004/07/08 12:02:32  kaspar
 * * Documentation changes, Added some debug code into
 *   the main view of the creator
 *
 * Revision 1.13  2004/05/22 08:39:36  perki
 * Lot of cleaning
 *
 * Revision 1.12  2004/05/18 10:48:38  perki
 * *** empty log message ***
 *
 * Revision 1.11  2004/05/13 07:45:33  perki
 * Marginal and effective rates
 *
 * Revision 1.10  2004/04/09 07:16:52  perki
 * Lot of cleaning
 *
 * Revision 1.9  2004/03/22 14:32:30  carlito
 * *** empty log message ***
 *
 * Revision 1.8  2004/03/12 14:06:10  perki
 * Vaseline machine
 *
 * Revision 1.7  2004/03/11 10:28:17  carlito
 * *** empty log message ***
 *
 * Revision 1.6  2004/03/08 18:23:49  carlito
 * *** empty log message ***
 *
 * Revision 1.5  2004/03/08 13:34:54  perki
 * *** empty log message ***
 *
 * Revision 1.4  2004/03/08 11:20:12  carlito
 * *** empty log message ***
 *
 * Revision 1.3  2004/03/08 11:11:36  carlito
 * *** empty log message ***
 *
 * Revision 1.2  2004/03/08 11:01:19  perki
 * *** empty log message ***
 *
 * Revision 1.1  2004/03/04 19:31:28  carlito
 * *** empty log message ***
 *
 */