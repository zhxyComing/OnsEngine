package com.dixon.onsengine.core.util;

import java.text.NumberFormat;

public class SizeFormat {

    public static String format(long size) {
        NumberFormat ddf1 = NumberFormat.getNumberInstance();
        ddf1.setMaximumFractionDigits(2);

        String sizeDisplay;
        if (size > 1073741824.0) {
            double result = size / 1073741824.0;
            sizeDisplay = ddf1.format(result) + "G";
        } else if (size > 1048576.0) {
            double result = size / 1048576.0;
            sizeDisplay = ddf1.format(result) + "M";
        } else if (size > 1024) {
            double result = size / 1024;
            sizeDisplay = ddf1.format(result) + "K";
        } else {
            sizeDisplay = ddf1.format(size) + "B";
        }
        return sizeDisplay;
    }
}
