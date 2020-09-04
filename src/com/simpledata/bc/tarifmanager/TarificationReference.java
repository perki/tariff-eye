/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: TarificationReference.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.tarifmanager;

import java.io.Serializable;
import java.net.URL;

import com.simpledata.bc.datamodel.TarificationHeader;

/**
 * This is a Container for a TarificationHeader in order to be able to retrieve
 * the Tarif Associated.<BR>
 * It also contains an URL to download it (may point to a file on the hard drive)
 */
public class TarificationReference implements Serializable {
    
    private TarificationHeader tarificationHeader;
    private long fileSize;
    private String url;
    
    public TarificationReference(TarificationHeader th,long size,String u) {
        tarificationHeader = th;
        fileSize = size;
        url = u;
    }
    
    
    //------------ XML --------------//
    public TarificationReference() {}

    /**
     * @return Returns the fileSize.
     */
    public long getFileSize() {
        return fileSize;
    }
    /**
     * @param fileSize The fileSize to set.
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    /**
     * @return Returns the tarificationHeader.
     */
    public TarificationHeader getTarificationHeader() {
        return tarificationHeader;
    }
    /**
     * @param tarificationHeader The tarificationHeader to set.
     */
    public void setTarificationHeader(TarificationHeader tarificationHeader) {
        this.tarificationHeader = tarificationHeader;
    }
    /**
     * @return Returns the url.
     */
    public String getUrl() {
        return url;
    }
    /**
     * @param url The url to set.
     */
    public void setUrl(String url) {
        this.url = url;
    }
}
