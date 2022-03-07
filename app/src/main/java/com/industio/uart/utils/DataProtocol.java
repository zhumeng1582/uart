package com.industio.uart.utils;

public class DataProtocol {
    //RTC：0x10,未识别：0x01/写失败：0x02
    public final static int TRC = 0x10, TRC_UNKNOWN = 0x01, TRC_WRITE_ERROR = 0x02;
    //音频:0x15,未识别Codec：0x01,未识别耳机：0x02
    public final static int AUDIO = 0x15, AUDIO_UNKNOWN_CODEC = 0x01, AUDIO_UNKNOWN_EARPHONE = 0x02;
    //录音:0x17,未识别MIC：0x01
    public final static int RECORD = 0x17, RECORD_UNKNOWN_MIC = 0x01;
    //ADC:0x1A,电压错误：0x01
    public final static int ADC = 0x1A, ADC_AUDIO_UNKNOWN_MIC = 0x01;
    //红外接收:0x1C,未接收到数据：0x01
    public final static int INFRARED = 0x1C, INFRARED_NO_DADA = 0x01;
    //DI/DO:0x20,互测失败:0x10-0xEF
    public final static int DIDO = 0x20, EACH_TEST_ERROR_START = 0x10, EACH_TEST_ERROR_END = 0xEF;
    //I2C传感器:0x23,0x00-0xEF
    public final static int I2C = 0x23, I2C_ERROR_START = 0x00, I2C_END = 0xEF;
    //UART:0x26,数据错误:0xX1,接收超时:0xX2
    public final static int UART = 0x26, UART_DATA_ERROR = 0x01, UART_DATA_TIMEOUT = 0x02;
    //网口:0x27,未识别：0xX1,Up不起来：0xX2,获取不到IP：0xX3,Ping不通：0xX4,识别速率错误：0xX5,iperf速率不对：0xX6
    public final static int NET_PORT = 0x27, NET_PORT_UNKNOWN = 0x01, NET_UP_ERROR = 0x02;
    public final static int NET_GET_IP_ERROR = 0x03, NET_PING_ERROR = 0x04, NET_SPEED_ERROR = 0x05, NET_IPERF_ERROR = 0x06;
    //CAN:0x29,未识别：0xX1,Up不起来：0xX2,无法通信：0xX3,数据错误：0xX4
    public final static int CAN = 0x29, CAN_UNKNOWN = 0x01, CAN_UP_ERROR = 0x02, CAN_COMMUNICATE_ERROR = 0x03, CAN_DATA_ERROR = 0x04;
    //4G/5G:0x2B,未识别模块：0xX1,未识别卡：0xX2,未注册：0xX3,ping不通：0xX4
    public final static int G45G = 0x2B, G45G_UNRECOGNIZED = 0x01,G45G_UNRECOGNIZED_CARD = 0x02, G45G_REGISTER = 0x03, G45G_PING = 0x04;
    //WIFI:0x2D,未识别：0x01,Up不起来：0x02,获取不到IP：0xX3,Ping不通：0x04,iperf速率错误：0x05
    public final static int WIFI = 0x2D, WIFI_UNRECOGNIZED = 0x01, G45G_UP_ERROR = 0x02, G45G_IP_ERROR = 0x03, G45G_PING_ERROR = 0x04, G45G_IPERF_ERROR = 0x05;
    //BT:0x2F,未识别：0x01,Up不起来：0x02
    public final static int BT = 0x2F, BT_UNRECOGNIZED = 0x01, BT_UP_ERROR = 0x02;
    //USB2.0:0x31,无法识别：0xX1,无法读写：0xX2
    public final static int USB2 = 0x31, USB2_UNRECOGNIZED = 0x01, USB2_WR_ERROR = 0x02;
    //USB3.0:0x32,未识别：0xX1,无法读写：0xX2
    public final static int USB3 = 0x32, USB3_UNRECOGNIZED = 0x01, USB3_WR_ERROR = 0x02;
    //Type-C:0x33,未识别：0xX1,无法读写：0xX2
    public final static int TYPE_C = 0x33, TYPE_C_UNRECOGNIZED = 0x01, TYPE_C_WR_ERROR = 0x02;
    //SD卡:0x35,未识别：0x01,无法读写：0x02
    public final static int SD = 0x35, SD_UNRECOGNIZED = 0x01, SD_WR_ERROR = 0x02;
    //SATA:0x37,未识别：0xX1,无法读写：0xX2
    public final static int SATA = 0x37, SATA_UNRECOGNIZED = 0x01, SATA_WR_ERROR = 0x02;
    //PCI-E:0x39,未识别：0xX1,无法读写：0xX2
    public final static int PCI_E = 0x39, PCI_E_UNRECOGNIZED = 0x01, PCI_E_WR_ERROR = 0x02;
    //M.2:0x41,未识别：0xX1,无法读写：0xX2
    public final static int M2 = 0x41, M2_UNRECOGNIZED = 0x01, M2_WR_ERROR = 0x02;
    //SPI:0x43,未识别：0xX1
    public final static int SPI = 0x43, SPI_UNRECOGNIZED = 0x01;
    //摄像头:0x46
    public final static int CAMERA = 0x46;
    //TP：0x50,未识别：0x01,无报点：0x02,报点错误：0x03
    public final static int TP = 0x50, TP_UNRECOGNIZED = 0x01, TP_NO_POINT = 0x02, TP_POINT_ERROR = 0x03;

    //屏幕:0x53,屏幕无显示：0x01
    public final static int SCREEN = 0x53, SCREEN_BLANK = 0x01;

    //自定义1:0xEE,0x00-0xEF
    //自定义2:0xED,0x00-0xEF
    //自定义3:0xEF,0x00-0xEF
    //结束符：0xF0,0x00
    public final static int END =  0xF0;
}
