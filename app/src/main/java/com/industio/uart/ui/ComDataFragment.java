package com.industio.uart.ui;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.industio.uart.bean.BootPara;
import com.industio.uart.cache.BootParaInstance;
import com.industio.uart.databinding.FragmentComDataBinding;
import com.industio.uart.utils.DataAnalysis;
import com.industio.uart.utils.DataProtocol;

import org.ido.iface.SerialControl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class ComDataFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ComDataFragment";
    private FragmentComDataBinding binding;
    private BootPara bootPara;
    private Thread testDeviceThread;
    private int testCount = 0;
    private int errorCount = 0;
    private long testTimeLong = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentComDataBinding.inflate(getLayoutInflater());
        init();
        return binding.getRoot();
    }

    private void init() {
        if (StringUtils.equals(getTag(), "1")) {
            bootPara = BootParaInstance.getInstance().getBootPara1();
        } else {
            bootPara = BootParaInstance.getInstance().getBootPara2();
        }

        binding.imageSetting.setOnClickListener(this);
        binding.imagePlayAndStop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.imageSetting) {
            if (StringUtils.equals(getTag(), "1")) {
                ShutUpDownActivity.startActivity(getActivity(), BootParaInstance.KEY_BOOT_PRAR1);
            } else {
                ShutUpDownActivity.startActivity(getActivity(), BootParaInstance.KEY_BOOT_PRAR2);
            }
        } else if (v == binding.imagePlayAndStop) {

            if (binding.imagePlayAndStop.isChecked()) {
                binding.imagePlayAndStop.setChecked(false);
                if (testDeviceThread != null) {
                    testDeviceThread.interrupt();
                }
                durTime.cancel();
            } else {

                testDevice();
                binding.imagePlayAndStop.setChecked(true);
            }

        }
    }

    private void testDevice() {
        testCount = 0;
        errorCount = 0;
        testTimeLong = 0;
        initErrorInfoSerial();

        testDeviceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!testDeviceThread.isInterrupted()) {
                    try {
                        //电源波动
                        if (bootPara.isShutTimesSwitch()) {
                            for (int i = 0; i < bootPara.getShutTimes(); i++) {
                                setPower(true);
                                Thread.sleep(bootPara.getShutUpDur());
                                setPower(false);
                                Thread.sleep(bootPara.getShutDownDur());
                            }
                        }
                        //足额上电
                        if (bootPara.isFullShutUp()) {
                            setPower(true);
                            Thread.sleep(bootPara.getFullShutUpDur() * 1000);
                        }
                        testCount ++;
                        binding.textTestTimesValue.setText(testCount+"次");

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        durTime =  new ThreadUtils.Task<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                return null;
            }

            @Override
            public void onSuccess(Object result) {
                testTimeLong ++;
                binding.textTestDurationValue.setText(com.industio.uart.utils.TimeUtils.getDurTime(testTimeLong));
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFail(Throwable t) {

            }
        };

        testDeviceThread.start();
        ThreadUtils.executeByCachedAtFixRate(durTime,1, TimeUnit.SECONDS);
    }


    //上下电设置
    private void setPower(boolean on) {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String path = bootPara.getAccessPort().getGpio();

                if (new File(path).exists()) {
                    FileWriter writer = null;
                    try {
                        writer = new FileWriter(path);
                        if (on)
                            writer.write("1");
                        else
                            writer.write("0");
                        writer.flush();
                    } catch (Exception ex) {
                        Log.e(TAG, "" + ex);
                    } finally {
                        if (writer != null) {
                            try {
                                writer.close();
                            } catch (IOException ex) {
                                Log.e(TAG, "" + ex);
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "node is not found:" + path);
                }

            }
        });
    }

    //初始化错误串口
    private void initErrorInfoSerial() {

        binding.textErrorDetails.setText("");
        SerialControl serialControl = new SerialControl() {
            @Override
            protected void read(byte[] buf, int len) {

                ThreadUtils.runOnUiThread(() -> {

                    //不为END 即代表出错，判断是否需要终止线程
                    if (buf[1] != DataProtocol.END && !bootPara.isErrorContinue()) {
                        if (testDeviceThread != null) {
                            testDeviceThread.interrupt();
                            durTime.cancel();
                            binding.imagePlayAndStop.setChecked(false);
                        }
                    }else if (buf[1] == DataProtocol.END) {
                        if (bootPara.isAlarmSound()) {
                            playAlarmSound();
                        }
                        errorCount++;
                        binding.textTestErrorTimesValue.setText(errorCount +"次");
                        binding.textErrorDetails.setText(DataAnalysis.analysis(buf[1], buf[2]));

                    }

                });
            }
        };
        String portName = bootPara.getAccessPort().getPort();
        if (serialControl.init(portName, 9600, 8, 'N', 1, 0, 10)) {
            //ToastUtils.showShort("打开成功:"+portName);
            Toast.makeText(getContext(), "打开成功:" + portName, Toast.LENGTH_LONG).show();
        } else {
            // ToastUtils.showLong("打开失败:"+portName);
            Toast.makeText(getContext(), "打开失败:" + portName, Toast.LENGTH_LONG).show();
            LogUtils.d("打开失败:" + portName);
        }
    }

    //初始化日志串口
    private void initLogSerial() {

        int portRate = (int) binding.spinnerPortRate.getSelectedItem();
        binding.textLogDetails.setText("");
        SerialControl serialControl = new SerialControl() {
            @Override
            protected void read(final byte[] buf, final int len) {

                ThreadUtils.runOnUiThread(() -> {
                    try {
                        String log = new String(buf, "UTF-8");
                        binding.textLogDetails.setText(log);
                        if (bootPara.isSaveLog()) {
                            SimpleDateFormat simpleDateFormat = TimeUtils.getSafeDateFormat("yyyyMMddHHmmss");
                            String fileName = "/sdcard/test/项目名/" + bootPara.getDeviceName() + TimeUtils.getNowString(simpleDateFormat) + ".log";
                            FileIOUtils.writeFileFromString(fileName, log, true);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                });
            }
        };
        String portName = (String) binding.spinnerPortValue.getSelectedItem();
        if (serialControl.init(portName, portRate, 8, 'N', 1, 0, 0)) {
            //ToastUtils.showShort("打开成功:"+portName);
            Toast.makeText(getContext(), "打开成功:" + portName, Toast.LENGTH_LONG).show();
        } else {
            // ToastUtils.showLong("打开失败:"+portName);
            Toast.makeText(getContext(), "打开失败:" + portName, Toast.LENGTH_LONG).show();
            LogUtils.d("打开失败:" + portName);
        }
    }

    public void playAlarmSound() {

        try {
            AssetFileDescriptor fdLeft = getActivity().getAssets().openFd("alarm_sound.wav");
            MediaPlayer mpLeft = new MediaPlayer();
            mpLeft.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mpLeft.setDataSource(fdLeft.getFileDescriptor(), fdLeft.getStartOffset(), fdLeft.getLength());
            mpLeft.prepare();
            mpLeft.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private ThreadUtils.Task<Object> durTime;

}
