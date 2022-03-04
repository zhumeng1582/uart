package com.industio.uart.utils.iface;

public class DeviceCtrl {
    public native int  get_lcd_density();//-1：未获取到密度值
    public native int  get_sf_orientation();//-1：未获取屏幕方向值
    public native int  get_no_navbar();//0：系统有导航栏，1：系统无导航栏，-1：未获取参数
    public native int  get_no_statusbar();//0：系统有状态栏，1：系统无状态栏，-1：未获取参数

    static {
        System.loadLibrary("devctrl");
    }
}
