/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 */
package com.simpledata.util;
import java.util.*;

/**
* Fifo stack with a limited size.
* Like a Stack, elements are pushed, poped and peeked from bottom.
* When the limit is reached, elements are suppressed at bottom
* (and lost !)
* Default size is of ten object capacity.
*/
public class SizedStack {
    private Vector<Object> stack=null;
    private int size;

    public SizedStack() {
        this.size = 10;
        stack = new Vector<Object>();
    }

    public SizedStack(int size) {
        this.size = size;
        stack = new Vector<Object>();
    }

    public void setSize(int size) {
        if(size < stack.size())
            for(int i=size-1;i<stack.size();i++)
                stack.remove(i); // Shit ! Vector.removeRange is protected !!!!!!!
        this.size=size;
    }

    public int getSize() {
        return this.size;
    }

    public boolean empty() {
        return (stack.size() <= 0);
    }

    public Object peek() {
        return stack.firstElement();
    }

    public Object pop() {
       	Object o = stack.firstElement();
        stack.removeElementAt(0);
        return o;
    }

    public Object push(Object o) {
        if(stack.size() >= size)
            for(int i=size-1;i<stack.size();i++)
                stack.remove(i); // Shit ! Vector.removeRange is protected !!!!!!!
        stack.add(0, o);
        return o;
    }

    public int search(Object o) {
        return stack.indexOf(o);
    }

    public void clear() {
        stack.clear();
    }
}
