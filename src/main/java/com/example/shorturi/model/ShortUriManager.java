package com.example.shorturi.model;

import java.util.zip.CRC32;

/**
 * Project ShortUri
 *
 * @author Alexandre
 * Cette classe sert à gérer les paires d'urls
 **/
public class ShortUriManager {

    public static ShortUriManager INSTANCE;

    private ShortUriManager(){
    }

    public static ShortUriManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ShortUriManager();
        }
        return INSTANCE;
    }

    public String createShortUri(String s) {
        CRC32 crc32 = new CRC32();
        crc32.update(s.getBytes());
        return "sho.rt/" + Long.toHexString(crc32.getValue());
    }
}
