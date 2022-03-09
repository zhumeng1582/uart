package com.industio.uart.cache;

import com.industio.uart.bean.AccessPara;

public class AccessParaContent1 extends AccessParaContent {
    private AccessPara accessPara1 = new AccessPara("通道1", "/dev/ttyS3", "/sys/class/leds/relay1/brightness");
    private AccessPara accessPara2 = new AccessPara("通道2", "/dev/ttyS0", "/sys/class/leds/relay1/brightness");
    private AccessPara accessPara3 = new AccessPara("通道3", "/dev/ttyS3", "/sys/class/leds/relay1/brightness");
    private AccessPara accessPara4 = new AccessPara("通道4", "/dev/ttyS4", "/sys/class/leds/relay1/brightness");
    private AccessPara accessPara5 = new AccessPara("通道5", "/dev/ttyS5", "/sys/class/leds/relay1/brightness");

    @Override
    public AccessPara accessPara1() {
        return accessPara1;
    }

    @Override
    public AccessPara accessPara2() {
        return accessPara2;
    }

    @Override
    public AccessPara accessPara3() {
        return accessPara3;
    }

    @Override
    public AccessPara accessPara4() {
        return accessPara4;
    }

    @Override
    public AccessPara accessPara5() {
        return accessPara5;
    }
}
