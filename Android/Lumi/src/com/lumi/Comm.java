package com.lumi;

import java.nio.ByteBuffer;

import android.util.Log;

public class Comm {
    
    static byte[] bytes = new byte[6];
    
    public static String displayLEDString(int row, int column, int color){
        String str = "d" + row + column + Integer.toHexString(color).substring(2);
        return str;
    }
    
    public static byte[] displayLEDBytes(int row, int column, int color){
        byte[] colors = ByteBuffer.allocate(4).putInt(color).array();
        // colors[0] contains the alpha, which is not used for Lumi
        // colors[1-3] contain values for red, green, and blue, respectively
        
        bytes[0] = 'd';
        bytes[1] = (byte) (row & 0xFF);
        bytes[2] = (byte) (column & 0xFF);
        bytes[3] = colors[1]; //Red
        bytes[4] = colors[2]; //Green
        bytes[5] = colors[3]; //Blue
        
        for (byte b: bytes) {
            Log.i("Bytestream", "B: " + b);
        }
        
        return bytes;
    }
}
