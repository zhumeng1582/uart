package com.industio.uart.ui;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;

public class ComDataFragment extends Fragment implements View.OnClickListener {
    private FragmentComDataBinding binding;
    private BootPara bootPara;
    private Thread testDeviceThread;

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
                    testDeviceThread.isInterrupted();
                }
            } else {
                testDevice();
                binding.imagePlayAndStop.setChecked(true);
            }

        }
    }

    private void testDevice() {
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
                            Thread.sleep(bootPara.getFullShutUpDur()*1000);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        testDeviceThread.start();
    }

    //上下电设置
    private void setPower(boolean on) {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    //初始化错误串口
    private void initErrorInfoSerial() {

        int portRate = (int) binding.spinnerPortRate.getSelectedItem();
        binding.textErrorDetails.setText("");
        SerialControl serialControl = new SerialControl() {
            @Override
            protected void read(byte[] buf, int len) {

                ThreadUtils.runOnUiThread(() -> {

                    if (buf[1] == DataProtocol.END && !bootPara.isErrorContinue()) {
                        if (testDeviceThread != null) {
                            testDeviceThread.interrupt();
                            binding.imagePlayAndStop.setChecked(false);
                        }
                    }

                    if (buf[1] == DataProtocol.END) {
                        if (bootPara.isAlarmSound()) {
                            playAlarmSound();
                        }
                    }
                    binding.textErrorDetails.setText(DataAnalysis.analysis(buf[1], buf[2]));

                });
            }
        };
        String portName = "xxx";
        if (serialControl.init(portName, portRate, 0, 0, 0, 0, 10)) {//接收100ms粘包
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
        String portName = "xxx";
        if (serialControl.init(portName, portRate, 0, 0, 0, 0, 10)) {//接收100ms粘包
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
}
