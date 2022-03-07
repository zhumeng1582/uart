package com.industio.uart.utils;

public class DataAnalysis {
    public static String analysis(int data1, int data2) {
        int temp;
        switch (data1) {
            case DataProtocol.TRC:
                if (data2 == DataProtocol.TRC_UNKNOWN) {
                    return "RTC：未识别";
                } else if (data2 == DataProtocol.TRC_WRITE_ERROR) {
                    return "RTC：写失败";
                }
                break;
            case DataProtocol.AUDIO:
                if (data2 == DataProtocol.AUDIO_UNKNOWN_CODEC) {
                    return "音频:未识别Codec";
                } else if (data2 == DataProtocol.AUDIO_UNKNOWN_EARPHONE) {
                    return "音频:未识别耳机";
                }
                break;
            case DataProtocol.RECORD:
                if (data2 == DataProtocol.RECORD_UNKNOWN_MIC) {
                    return "录音:未识别MIC";
                }
                break;
            case DataProtocol.ADC:
                if (data2 == DataProtocol.ADC_AUDIO_UNKNOWN_MIC) {
                    return "ADC：电压错误";
                }
                break;
            case DataProtocol.INFRARED:
                if (data2 == DataProtocol.INFRARED_NO_DADA) {
                    return "红外接收:未接收到数据";
                }
                break;
            case DataProtocol.DIDO:
                if (data2 >= DataProtocol.EACH_TEST_ERROR_START && data2 <= DataProtocol.EACH_TEST_ERROR_END) {
                    return "DI/DO:互测失败";
                }
                break;
            case DataProtocol.I2C:
                if (data2 >= DataProtocol.I2C_ERROR_START && data2 <= DataProtocol.I2C_END) {
                    return "I2C传感器错误";
                }
                break;
            case DataProtocol.UART:
                temp = data2 & 0x0F;
                if (temp == DataProtocol.UART_DATA_ERROR) {
                    return "UART:数据错误:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.UART_DATA_TIMEOUT) {
                    return "UART:接收超时:设备号：" + (data2 >> 4);
                }
                break;
            case DataProtocol.NET_PORT:
                temp = data2 & 0x0F;
                if (temp == DataProtocol.NET_PORT_UNKNOWN) {
                    return "UART:未识别:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.NET_UP_ERROR) {
                    return "UART:Up不起来:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.NET_GET_IP_ERROR) {
                    return "UART:获取不到IP:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.NET_PING_ERROR) {
                    return "UART:Ping不通:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.NET_SPEED_ERROR) {
                    return "UART:识别速率错误:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.NET_IPERF_ERROR) {
                    return "UART:iperf速率不对:设备号：" + (data2 >> 4);
                }
                break;
            case DataProtocol.CAN:
                temp = data2 & 0x0F;
                if (temp == DataProtocol.CAN_UNKNOWN) {
                    return "CAN:未识别:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.CAN_UP_ERROR) {
                    return "CAN:Up不起来:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.CAN_COMMUNICATE_ERROR) {
                    return "CAN:无法通信:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.CAN_DATA_ERROR) {
                    return "CAN:数据错误:设备号：" + (data2 >> 4);
                }
                break;
            case DataProtocol.G45G:
                temp = data2 & 0x0F;
                if (temp == DataProtocol.G45G_UNRECOGNIZED) {
                    return "4G/5G:未识别模块:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.G45G_UNRECOGNIZED_CARD) {
                    return "4G/5G:未识别卡:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.G45G_REGISTER) {
                    return "4G/5G:未注册:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.G45G_PING) {
                    return "4G/5G:ping不通:设备号：" + (data2 >> 4);
                }

                break;
            case DataProtocol.WIFI:
                temp = data2 & 0x0F;
                if (temp == DataProtocol.WIFI_UNRECOGNIZED) {
                    return "WIFI:未识别:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.G45G_UP_ERROR) {
                    return "WIFI:Up不起来:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.G45G_IP_ERROR) {
                    return "WIFI:获取不到IP:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.G45G_PING_ERROR) {
                    return "WIFI:Ping不通:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.G45G_IPERF_ERROR) {
                    return "WIFI:iperf速率错误:设备号：" + (data2 >> 4);
                }
                break;
            case DataProtocol.BT:
                if (data2 == DataProtocol.BT_UNRECOGNIZED) {
                    return "BT:未识别";
                } else if (data2 == DataProtocol.BT_UP_ERROR) {
                    return "BT:Up不起来";
                }

                break;
            case DataProtocol.USB2:
                temp = data2 & 0x0F;
                if (temp == DataProtocol.USB2_UNRECOGNIZED) {
                    return "USB2.0:无法识别:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.USB2_WR_ERROR) {
                    return "USB2.0:无法读写:设备号：" + (data2 >> 4);
                }

                break;
            case DataProtocol.USB3:
                temp = data2 & 0x0F;
                if (temp == DataProtocol.USB3_UNRECOGNIZED) {
                    return "USB3.0:无法识别:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.USB3_WR_ERROR) {
                    return "USB3.0:无法读写:设备号：" + (data2 >> 4);
                }

                break;
            case DataProtocol.TYPE_C:
                temp = data2 & 0x0F;
                if (temp == DataProtocol.TYPE_C_UNRECOGNIZED) {
                    return "Type-C:未识别:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.TYPE_C_WR_ERROR) {
                    return "Type-C:无法读写:设备号：" + (data2 >> 4);
                }

                break;
            case DataProtocol.SD:
                temp = data2 & 0x0F;
                if (temp == DataProtocol.SD_UNRECOGNIZED) {
                    return "SD卡:未识别:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.SD_WR_ERROR) {
                    return "SD卡:无法读写:设备号：" + (data2 >> 4);
                }

                break;
            case DataProtocol.SATA:
                temp = data2 & 0x0F;
                if (temp == DataProtocol.SATA_UNRECOGNIZED) {
                    return "SATA:未识别:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.SATA_WR_ERROR) {
                    return "SATA:无法读写:设备号：" + (data2 >> 4);
                }

                break;
            case DataProtocol.PCI_E:
                temp = data2 & 0x0F;
                if (temp == DataProtocol.PCI_E_UNRECOGNIZED) {
                    return "PCI-E:未识别:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.PCI_E_WR_ERROR) {
                    return "PCI-E:无法读写:设备号：" + (data2 >> 4);
                }

                break;
            case DataProtocol.M2:
                temp = data2 & 0x0F;
                if (temp == DataProtocol.M2_UNRECOGNIZED) {
                    return "M.2:未识别:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.M2_WR_ERROR) {
                    return "M.2:无法读写:设备号：" + (data2 >> 4);
                }

                break;
            case DataProtocol.SPI:
                temp = data2 & 0x0F;
                if (temp == DataProtocol.SPI_UNRECOGNIZED) {
                    return "SPI:未识别:设备号：" + (data2 >> 4);
                }

                break;
            case DataProtocol.CAMERA:
                return "摄像头：";
            case DataProtocol.TP:
                temp = data2 & 0x0F;
                if (temp == DataProtocol.TP_UNRECOGNIZED) {
                    return "TP:未识别:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.TP_NO_POINT) {
                    return "TP:无报点:设备号：" + (data2 >> 4);
                } else if (temp == DataProtocol.TP_POINT_ERROR) {
                    return "TP:报点错误:设备号：" + (data2 >> 4);
                }

                break;
            case DataProtocol.SCREEN:
                if (data2 == DataProtocol.SCREEN_BLANK) {
                    return "屏幕无显示";
                }
        }
        return "";
    }
}
