package org.ido.iface;

import android.util.Log;
import java.io.File;

public abstract class SerialControl {
    private static final String TAG="SerialCtrl";

    private static final int MAX_RX_LEN = 10240;//一次接收的最大数据约10k

    private volatile int send_wait_time = 10;//100ms内未再接收到数据则回调读接口

    private SerialPort mSerialPort;

    private boolean mThreadRunning = false;

    private volatile byte[] uartRxbuf = new byte[MAX_RX_LEN];
    private volatile int uartRxLen = 0;
    private volatile int mDelayCnt = send_wait_time;

    //mdelay:unit 10ms,建议10，也就是100ms后未收到数据就会把收到的数据回调
    //当为0时，就在收到数据后会直接回调
    //接收的一组粘包数据会出现超时的情况
    public boolean init(String name,int speed, int databits, int parity, int stopbits, int flow_ctrl,int mdelay) { //一次性最大获取10k，在获取数据后，等待mdelay后，如果未在收到数据就回调数据
        File mFile = new File(name);
        mThreadRunning = true;
        send_wait_time = mdelay;
        if (mSerialPort == null) {
            mSerialPort = new SerialPort();
            if (mSerialPort.init(mFile,speed, databits, parity, stopbits, flow_ctrl)) {
                Log.d(TAG, "Open " + mFile.getAbsolutePath() + " sucess");
            } else {
                Log.d(TAG, "Open " + mFile.getAbsolutePath() + " failed!");
                return false;
            }
            uartWaitThread.start();
        }
        return true;
    }

    private Thread uartWaitThread = new Thread(new Runnable() {
        @Override
        public void run() {
            int len = 0;
            while (mThreadRunning && mSerialPort != null) {
                try {
                    mDelayCnt--;
                    if (mDelayCnt < 0) mDelayCnt = 0;

                    len = mSerialPort.getInputStream().available();
                    if (len > 0) {
                        if (send_wait_time != 0) {
                            mDelayCnt = send_wait_time;
                            if ((uartRxLen + len) <= MAX_RX_LEN) {
                                byte[] buf = new byte[len];
                                len = mSerialPort.getInputStream().read(buf, 0, len);
                                System.arraycopy(buf, 0, uartRxbuf, uartRxLen, len);
                                uartRxLen += len;
                            } else {
                                read(uartRxbuf, uartRxLen);
                                uartRxLen = 0;
                            }
                        } else {
                            byte[] buf = new byte[len];
                            len = mSerialPort.getInputStream().read(buf, 0, len);
                            read(buf, len);
                        }
                    } else if (mDelayCnt == 0 && uartRxLen > 0) {
                        read(uartRxbuf, uartRxLen);
                        uartRxLen = 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mSerialPort = null;
                    mThreadRunning = false;
                    uartWaitThread = null;
                    return;
                }
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                }
            }
        }
    });

    public boolean write(byte[] buf) {
        if (mSerialPort == null) {
            Log.e(TAG,"No run init function!");
            return false;
        }

        try {
            mSerialPort.getOutputStream().write(buf);
            return true;
        } catch (Exception e) {
            Log.e(TAG,"Write serial data fail:" + e);
            return false;
        }
    }

    abstract protected void read(byte[] buf, int len);

    public boolean close() {
        if (mSerialPort != null) {
            mThreadRunning = false;
            mSerialPort.serialClose();
            mSerialPort = null;
            uartWaitThread.interrupt();
            return true;
        } else {
            return false;
        }
    }
}
