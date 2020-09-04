/**
* $Id: PrintUtilities.java,v 1.1 2006/12/03 12:48:40 perki Exp $
* $Log: PrintUtilities.java,v $
* Revision 1.1  2006/12/03 12:48:40  perki
* First commit on sourceforge
*
* Revision 1.6  2004/09/04 18:10:15  kaspar
* ! Log.out -> log4j, last part
*
* Revision 1.5  2004/05/12 13:37:54  perki
* Log is clever
*
* Revision 1.4  2004/01/29 13:03:05  carlito
* warnings and imports corrected ...
*
* Revision 1.3  2004/01/13 11:06:58  perki
* lot of javadoc repairs
*
* Revision 1.2  2003/10/29 16:36:04  perki
* blop
*
*/

package com.simpledata.uitools;
import java.awt.*;
import java.awt.print.*;

import javax.swing.RepaintManager;

import org.apache.log4j.Logger;

/** A simple utility class that lets you very simply print
 *  an arbitrary component. Just pass the component to the
 *  PrintUtilities.printComponent. The component you want to
 *  print doesn't need a print method and doesn't have to
 *  implement any interface or do anything special at all.
 *  <P>
 *  If you are going to be printing many times, it is marginally more 
 *  efficient to first do the following:
 *  <PRE>
 *    PrintUtilities printHelper = new PrintUtilities(theComponent);
 *  </PRE>
 *  then later do printHelper.print(). But this is a very tiny
 *  difference, so in most cases just do the simpler
 *  PrintUtilities.printComponent(componentToBePrinted).
 *
 *  7/99 Marty Hall, http://www.apl.jhu.edu/~hall/java/
 *  May be freely used or adapted.
 */ 
public class PrintUtilities implements Printable {
	private static final Logger m_log = Logger.getLogger( PrintUtilities.class );
	
  private Component componentToBePrinted;

  public static void printComponent(Component c) {
    new PrintUtilities(c).print();
  }
  
  public PrintUtilities(Component componentToBePrinted) {
    this.componentToBePrinted = componentToBePrinted;
  }
  
  public void print() {
    PrinterJob printJob = PrinterJob.getPrinterJob();
    printJob.setPrintable(this);
    if (printJob.printDialog())
      try {
        printJob.print();
      } catch(PrinterException pe) {
      	m_log.error( "Error printing: ",pe );
      }
  }

 

  /**
	* The method @print@ must be implemented for @Printable@ interface.
	* Parameters are supplied by system.
	*/
	public int print(Graphics g, PageFormat pf, int pageIndex)
	throws PrinterException {
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.black);    //set default foreground color to black
		
		RepaintManager.currentManager(componentToBePrinted).setDoubleBufferingEnabled(false);
		Dimension d = componentToBePrinted.getSize();    //get size of document
		double panelWidth  = d.width;    //width in pixels
		double panelHeight = d.height;   //height in pixels
		double pageHeight = pf.getImageableHeight();   //height of printer page
		double pageWidth  = pf.getImageableWidth();    //width of printer page
		double scale = pageWidth/panelWidth;
		int totalNumPages = (int)Math.ceil(scale * panelHeight / pageHeight);
		
		// Make sure not print empty pages
		if(pageIndex >= totalNumPages) {
			return Printable.NO_SUCH_PAGE;
		}
		
		// Shift Graphic to line up with beginning of print-imageable region
		g2.translate(pf.getImageableX(), pf.getImageableY());
		// Shift Graphic to line up with beginning of next page to print
		g2.translate(0f, -pageIndex*pageHeight);
		// Scale the page so the width fits...
		g2.scale(scale, scale);
		componentToBePrinted.paint(g2);   //repaint the page for printing
		return Printable.PAGE_EXISTS;
	}
  
  /** The speed and quality of printing suffers dramatically if
   *  any of the containers have double buffering turned on.
   *  So this turns if off globally.
   *  @see #enableDoubleBuffering(Component c)
   */
  public static void disableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(false);
  }

  /** Re-enables double buffering globally. */
  
  public static void enableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(true);
  }
}
