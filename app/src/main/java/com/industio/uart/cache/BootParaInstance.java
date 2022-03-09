package com.industio.uart.cache;

import com.blankj.utilcode.util.CacheDiskUtils;
import com.industio.uart.bean.BootPara;

public class BootParaInstance {
    public static String KEY_BOOT_PRAR1 = "BootPara1";
    public static String KEY_BOOT_PRAR2 = "BootPara2";

    private static BootParaInstance instance;
    private BootPara bootPara1;
    private BootPara bootPara2;

    private BootParaInstance() {
        bootPara1 = (BootPara) CacheDiskUtils.getInstance().getSerializable(KEY_BOOT_PRAR1);
        bootPara2 = (BootPara) CacheDiskUtils.getInstance().getSerializable(KEY_BOOT_PRAR2);
    }

    public static BootParaInstance getInstance() {
        if (instance == null) {
            instance = new BootParaInstance();
        }
        return instance;
    }

    public BootPara getBootPara1() {
        if (bootPara1 == null) {
            bootPara1 =  new BootPara(new AccessParaContent1().accessPara1());
        }
        return bootPara1;
    }

    public void saveBootPara1(BootPara bootPara1) {
        this.bootPara1 = bootPara1;
        CacheDiskUtils.getInstance().put(KEY_BOOT_PRAR1, bootPara1);
    }

    public BootPara getBootPara2() {
        if (bootPara2 == null) {
            bootPara2 =  new BootPara(new AccessParaContent2().accessPara2());
        }
        return bootPara2;
    }

    public void saveBootPara2(BootPara bootPara2) {
        this.bootPara2 = bootPara2;
        CacheDiskUtils.getInstance().put(KEY_BOOT_PRAR2, bootPara2);
    }
}
