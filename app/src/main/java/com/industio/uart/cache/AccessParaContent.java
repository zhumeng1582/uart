package com.industio.uart.cache;

import com.industio.uart.bean.AccessPara;
public class AccessParaContent {
    public static AccessPara accessPara1 = new AccessPara("通道1","/dev/ttyS0","/sys/class/leds/relay1/brightness");
    public static AccessPara accessPara2 = new AccessPara("通道2","/dev/ttyS0","/sys/class/leds/relay1/brightness");
    public static AccessPara accessPara3 = new AccessPara("通道3","/dev/ttyS3","/sys/class/leds/relay1/brightness");
    public static AccessPara accessPara4 = new AccessPara("通道4","/dev/ttyS4","/sys/class/leds/relay1/brightness");
    public static AccessPara accessPara5 = new AccessPara("通道5","/dev/ttyS5","/sys/class/leds/relay1/brightness");
}
