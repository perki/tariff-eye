/* Copyright (c) 2003 to 2007
 * SimpleData Sarl http://simpledata.ch  All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 */
package com.simpledata.util;

import javax.swing.*;

/**
* Manipulation of resources in SDL.JAR
*/
public class ResourceManager {

	/**
	* private constructor to avoid instanciation
	**/
	private ResourceManager() {
		
	}
	
	/**
	* get an image from "/resources/images/" of the SDL archive
	*/
	public static ImageIcon getImageIcon(String imageName) {
		return new ImageIcon(ResourceManager.class.getResource("/resources/images/"+imageName));
	}
}
