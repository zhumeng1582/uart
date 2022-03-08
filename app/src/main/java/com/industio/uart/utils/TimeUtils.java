package com.industio.uart.utils;

public class TimeUtils {
    public static String getDurTime(long dur) {

        String day = "" + dur / 86400;
        dur = dur % 86400;
        String hour = "" + dur / 3600;
        dur = dur % 3600;
        String minute = "" + dur / 60;
        String second = "" + dur % 60;

        return day + "天" ;//+ hour + "h" + minute + "m" + second + "s";
    }
}
