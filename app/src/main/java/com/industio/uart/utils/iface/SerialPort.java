package com.industio.uart.utils.iface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileDescriptor;

public class SerialPort {

    public FileInputStream mFileInputStream;
    public FileOutputStream mFileOutputStream;
    private FileDescriptor mFd;

    protected SerialPort(){}

    protected boolean init(File device, int speed,int databits,int parity,int stopbits,int flow_ctrl) {
        mFd = open(device.getAbsolutePath(), speed,databits,parity,stopbits,flow_ctrl);
        if (mFd == null) {
            return false;
        } else {
            try {
                mFileInputStream = new FileInputStream(mFd);
                mFileOutputStream = new FileOutputStream(mFd);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    protected InputStream getInputStream() {
        return mFileInputStream;
    }

    protected OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    protected void serialClose() {
        try {
            if (mFileInputStream != null) {
                mFileInputStream.close();
                mFileOutputStream.close();
                close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //path：设备节点
    //speed:最大4Mbps
    //databits:数据位：5，6，7，8
    //parity:'n'/'N':无校验位；'o'/'O':奇校验；'e'/'E':偶校验
    //stopbits:停止位：1，2
    //flow_ctrl:0：无流控；1：硬件流控；2：软件流控
    private native FileDescriptor open(String path, int speed,int databits,int parity,int stopbits,int flow_ctrl);

    private native void close();

    static {
        System.loadLibrary("serialport");
    }
}
