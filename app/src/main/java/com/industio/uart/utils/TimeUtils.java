package com.industio.uart.utils;

import java.util.Random;

public class TimeUtils {
    static Random random = new Random();

    public static String getDurTime(long dur) {

        String day = "" + dur / 86400;
        dur = dur % 86400;
        String hour = "" + dur / 3600;
        dur = dur % 3600;
        String minute = "" + dur / 60;
        String second = "" + dur % 60;

        return day + "天" + hour + "时" + minute + "分" + second + "秒";
    }

    public static int getRandomTime(int min, int max) {
        return random.nextInt(max) % (max - min + 1) + min;
    }
}
