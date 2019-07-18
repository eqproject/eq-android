package org.eq.android.utils;

import java.math.BigDecimal;

public class MathUtils {

    public static double fen2yuan(int fen) {
        return new BigDecimal(fen).divide(new BigDecimal(100)).doubleValue();
    }

    public static String fen2yuanStr(int fen){
        return new BigDecimal(fen).divide(new BigDecimal(100)).toString();
    }
}
