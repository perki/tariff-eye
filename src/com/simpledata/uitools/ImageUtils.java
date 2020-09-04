/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
* $Id: ImageUtils.java,v 1.2 2007/04/02 17:04:30 perki Exp $
*/


package com.simpledata.uitools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.simpledata.util.Base64;

/**
* This class provides a series of tools for image manipulation
*/
public class ImageUtils {
	
	private static final Logger m_log = Logger.getLogger( ImageUtils.class );
	
	
	/**
	 * Fit an image in the center of a box (preserve ratio)
	 * @param i the image
	 * @param width the width of the box
	 * @param height the height of the box
	 * @param forceEnlarge set to true the image will be up sized if it's 
	 * smaller than the box
	 */
	public static Image 
		fitInBox(Image i,int width,int height, boolean forceEnlarge) {
		
		if (i.getWidth(null) == 0 || i.getHeight(null) == 0) {
			m_log.error( "Image has a null dimension" );
			return i;
		}
		
		BufferedImage result = getBufferedImage(width,height);
		
		Graphics2D g2 = (Graphics2D) result.getGraphics();
		g2.setColor(new Color(0, 0, 0, 0));
		g2.setPaintMode();
		g2.fillRect(0, 0, height, width);
		
		
		
		boolean enlarge = forceEnlarge ? true : 
				((width < i.getWidth(null) || (height < i.getHeight(null))));
		
		Image temp = i;	
		float factorW = (width + 0f)/ i.getWidth(null);
		float factorH = (height + 0f) / i.getHeight(null);
		if (enlarge) {
			if (factorW < factorH) {
				temp = resizeImage(i,width,-1);
			} else {
				temp = resizeImage(i,-1,height);
			}	
		}
		
		int posX = (width - temp.getWidth(null))/2;
		int posY = (height - temp.getHeight(null))/2;
		
		// draw Image
		g2.drawImage(temp,posX,posY,null);
		g2.dispose();
		return result;
		
	}
	
	/**
	 * Resize an Image
	 * @param width desired width (-1 for preserver aspect ratio)
	 * @param height desired height (-1 for preserver aspect ratio)
	 */
	public static Image resizeImage(Image i,int width,int height) {
		Image image = 
			i.getScaledInstance(width,height,java.awt.Image.SCALE_SMOOTH);
		ImageLoader.loadImage(image);
		return image;
	}

	/**
	* Return an Image object from an image path
	* @param imageName : String symbolizing path to the loaded image
	* @return Image @see java.awt.Image
	*/
	public static Image getImage(String imageName) {
		// see Inner class ImageLoader
		return ImageLoader.loadImage(imageName);
	}

	
	/**
	*  INNER CLASS used for image loading
	*/
	static class ImageLoader extends Component {
		
		private static ImageLoader imageLoader;
		
		static {
			imageLoader = new ImageLoader();
		}
		
		private ImageLoader() {
			super();
		}
		
		public static Image loadImage(String imageName) {
			Image image = Toolkit.getDefaultToolkit().getImage(imageName);
			loadImage(image);
			return image;
		}
		
		
		public static void loadImage(Image image) {
			try {
				MediaTracker tracker = new MediaTracker(imageLoader);
				tracker.addImage(image,0);
				tracker.waitForID(0);
			} catch (InterruptedException e) {
				m_log.error( "cannot load image:"+image,e );
			}
		}
			
	}
	
	/** 
	 * convert an image to an array of int (used for saving) 
	 * @see #intsToImage(int[] pixels)
	 **/
	public static int[] imageToInts(Image image) {
		int w = image.getWidth(null);
        int h = image.getHeight(null);
        int[] pixels = image != null? new int[(w * h)+2] : null;
        
        if (image != null) {
        	pixels[0] = w;
        	pixels[1] = h;
            try {
                PixelGrabber pg 
				= new PixelGrabber(image, 0, 0, w, h, pixels, 2, w);
                pg.grabPixels();
                if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
                    m_log.error( "failed to load image contents" );
                    return null;
                }
            }
            catch (InterruptedException e) {
            	m_log.error("image load interrupted" );
            	 return null;
            }
        }
        
        return pixels;
	}
	
	
	
	/** 
	 * convert an array of int to an Image (used for saving) 
	 * @see #imageToInts(Image image)
	 **/
	public static Image intsToImage(int[] pixels) {
		if (pixels == null || pixels.length < 3) {
			m_log.error( "int array to small or null" );
			return null;
		}
		int w = pixels[0];
        int h = pixels[1];
        Toolkit tk = Toolkit.getDefaultToolkit();
        ColorModel cm = ColorModel.getRGBdefault();
        return tk.createImage(new MemoryImageSource(w, h, cm, pixels, 2, w));
	}

	
	
	/**
	 * get a Buffered Image from an Image
	 */
	public static BufferedImage imageToBufferedImage(Image source) {
	    int w = source.getWidth(null);
	    int h = source.getHeight(null);
	    BufferedImage image = 
	        new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2d = (Graphics2D)image.getGraphics();
	    g2d.drawImage(source, 0, 0, null);
	    g2d.dispose();
	    return image;
	}
	
	private final static String BASE64_FORMAT = "png";
	
	/**
	 * convert an Image to a String encoded in Base64
	 * (encode in PNG)
	 */
	public static String imageToBase64(BufferedImage image) {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    try {
            ImageIO.write(imageToBufferedImage(image),BASE64_FORMAT,baos);
        } catch (IOException e) {
            m_log.error("Error while converting image to PNG",e);
        }
	    return Base64.encodeBytes(baos.toByteArray());
	}
	
	/**
	 * convert an Image encoded in Base64 to a STring
	 * (encode in PNG)
	 */
	public static BufferedImage base64toImage(String base64Str) {
	    ByteArrayInputStream bais = 
	        new ByteArrayInputStream( Base64.decode(base64Str));
	    try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            m_log.error("Error while reading a PNG",e);
        }
	   return null;
	}
	
	
	/**
	 * INTERNAL tool:
	 * get a Buffered Image 
	 */
	public static BufferedImage getBufferedImage(int width,int height) {
		BufferedImage result =  
			new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		return result;
	}
	
}
/*
* $Log: ImageUtils.java,v $
* Revision 1.2  2007/04/02 17:04:30  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:40  perki
* First commit on sourceforge
*
* Revision 1.7  2004/09/16 10:10:53  perki
* added base64 support
*
* Revision 1.6  2004/09/04 18:10:15  kaspar
* ! Log.out -> log4j, last part
*
* Revision 1.5  2004/06/20 16:09:39  perki
* *** empty log message ***
*
* Revision 1.4  2004/06/18 18:25:23  perki
* *** empty log message ***
*
* Revision 1.3  2004/06/15 06:14:06  perki
* *** empty log message ***
*
* Revision 1.2  2004/01/29 13:03:05  carlito
* warnings and imports corrected ...
*
* Revision 1.1  2003/10/10 13:54:41  carlito
* Added a class for image manipulation
*
**/