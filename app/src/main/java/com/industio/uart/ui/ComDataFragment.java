package com.industio.uart.ui;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
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
    private ThreadUtils.Task<Object> durTime;

    private int testCount = 0;
    private int errorCount = 0;
    private long testTimeLong = 0;

    private int uartRxDataFlag = 0;
    private boolean testDevThreadRunFlag = false;

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

        String[] deviceNo = {"/dev/ttyS1", "/dev/ttyS2", "/dev/ttyS3", "/dev/ttyS4"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, deviceNo);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPortValue.setAdapter(adapter);

        String[] portRate = {"115200", "1500000"};
        ArrayAdapter<String> adapterPortRate = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, portRate);
        adapterPortRate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPortRate.setAdapter(adapterPortRate);
    }

    @Override
    public void onResume() {
        super.onResume();

        binding.textTotalTestTimesValue.setText(bootPara.getTestCount() + "次");
        binding.textTotalTestErrorTimesValue.setText(bootPara.getErrorCount() + "次");
        binding.textTotalTestDurationValue.setText(com.industio.uart.utils.TimeUtils.getDurTime(bootPara.getTestTimeLong()));

    }

    @Override
    public void onClick(View v) {
        if (v == binding.imageSetting) {
            if (binding.imagePlayAndStop.isChecked()) {
                ToastUtils.showShort("请先停止测试");
                return;
            }
            if (StringUtils.equals(getTag(), "1")) {
                ShutUpDownActivity.startActivity(getActivity(), BootParaInstance.KEY_BOOT_PRAR1);
            } else {
                ShutUpDownActivity.startActivity(getActivity(), BootParaInstance.KEY_BOOT_PRAR2);
            }
        } else if (v == binding.imagePlayAndStop) {
            if (binding.imagePlayAndStop.isChecked()) {
                binding.imagePlayAndStop.setChecked(false);
                if (testDeviceThread != null) {
                    //testDeviceThread.interrupt();
                    testDevThreadRunFlag = false;
                }
                durTime.cancel();
            } else {
                testDevThreadRunFlag = true;
                testDevice();
                binding.imagePlayAndStop.setChecked(true);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (durTime != null && !durTime.isCanceled()) {
            durTime.cancel();

        }
        if (testDeviceThread != null && testDeviceThread.isInterrupted()) {
            testDeviceThread.interrupt();
        }
    }

    private void testDevice() {
        clearCount();

        initErrorInfoSerial();

        testDeviceThread = new Thread(new Runnable() {
            int timeCnt;

            @Override
            public void run() {
                while (testDevThreadRunFlag) {
                    try {
                        uartRxDataFlag = 0;
                        timeCnt = 0;

                        if (bootPara.isShutTimesSwitch()) {   //电源波动
                            for (int i = 0; i < bootPara.getShutTimes(); i++) {
                                setPower(true);
                                Thread.sleep(bootPara.getShutUpDur());
                                setPower(false);
                                Thread.sleep(bootPara.getShutDownDur());
                                if (testDevThreadRunFlag == false) {//被手动停止
                                    break;
                                }
                            }
                        }

                        ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                testCount++;
                                bootPara.setTestCount(bootPara.getTestCount() + 1);
                                binding.textTestTimesValue.setText(testCount + "次");
                                binding.textTotalTestTimesValue.setText(bootPara.getTestCount() + "次");
                            }
                        });

                        //持续上电
                        setPower(true);

                        if (bootPara.isFullShutUp()) {
                            timeCnt = bootPara.getFullShutUpDur();
                            while (timeCnt-- > 0) {
                                Thread.sleep(1000);
                                if (testDevThreadRunFlag == false) {//被手动停止
                                    timeCnt = 0;
                                    break;
                                }
                            }

                            if (testDevThreadRunFlag && uartRxDataFlag == 0 && bootPara.isErrorContinue() == false) {//超时判断
                                //testDeviceThread.interrupt();
                                testDevThreadRunFlag = false;
                                durTime.cancel();
                                ThreadUtils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        errorCount++;
                                        bootPara.setErrorCount(bootPara.getErrorCount() + 1);
                                        binding.imagePlayAndStop.setChecked(false);
                                        binding.textErrorDetails.setText("超时错误！");
                                        binding.textTestErrorTimesValue.setText(errorCount + "次");
                                        binding.textTotalTestErrorTimesValue.setText(bootPara.getErrorCount() + "次");

                                    }
                                });
                                if (bootPara.isAlarmSound()) {
                                    playAlarmSound();
                                }
                            }
                        } else if (testDevThreadRunFlag) {
                            timeCnt = 0;
                            while (uartRxDataFlag != 2) {
                                Thread.sleep(1000);
                                timeCnt++;

                                if (bootPara.getFullShutUpDur() == timeCnt) {//超时判断
                                    if (uartRxDataFlag == 0 && bootPara.isErrorContinue() == false) {
                                        //testDeviceThread.interrupt();
                                        testDevThreadRunFlag = false;
                                        durTime.cancel();
                                        ThreadUtils.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                errorCount++;
                                                binding.imagePlayAndStop.setChecked(false);
                                                binding.textErrorDetails.setText("超时错误！");
                                                binding.textTestErrorTimesValue.setText(errorCount + "次");
                                            }
                                        });
                                        if (bootPara.isAlarmSound()) {
                                            playAlarmSound();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
            }
        });

        durTime = new ThreadUtils.Task<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                return null;
            }

            @Override
            public void onSuccess(Object result) {
                testTimeLong++;
                bootPara.setTestTimeLong(bootPara.getTestTimeLong() + 1);
                binding.textTotalTestDurationValue.setText(com.industio.uart.utils.TimeUtils.getDurTime(bootPara.getTestTimeLong()));
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
        ThreadUtils.executeByCachedAtFixRate(durTime, 1, TimeUnit.SECONDS);
    }

    private void clearCount() {
        testCount = 0;
        binding.textTestTimesValue.setText(testCount + "次");
        errorCount = 0;
        binding.textTestErrorTimesValue.setText(errorCount + "次");
        testTimeLong = 0;
        binding.textTestDurationValue.setText(com.industio.uart.utils.TimeUtils.getDurTime(testTimeLong));
    }


    //初始化错误串口
    private void initErrorInfoSerial() {

        binding.textErrorDetails.setText("");
        SerialControl serialControl = new SerialControl() {
            @Override
            protected void read(byte[] buf, int len) {
                ThreadUtils.runOnUiThread(() -> {
                    Log.d(TAG, "ErrorInfoSerial rx:" + len);
                    if (buf[0] == DataProtocol.START_FRAME && buf[3] == DataProtocol.END_FRAME) {
                        //不为END 即代表出错，判断是否需要终止线程
                        if (buf[1] == DataProtocol.OK) {
                            Log.d(TAG, "Test ok!!!");
                            uartRxDataFlag = 2;//接收到OK协议
                        } else if (buf[1] != DataProtocol.OK) {//收到错误码
                            ThreadUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    errorCount++;
                                    bootPara.setErrorCount(bootPara.getErrorCount() + 1);
                                    binding.textTestErrorTimesValue.setText(errorCount + "次");
                                    binding.textTotalTestErrorTimesValue.setText(bootPara.getErrorCount() + "次");
                                    binding.textErrorDetails.setText(DataAnalysis.analysis(buf[1], buf[2]));
                                }
                            });

                            uartRxDataFlag = 1;//接收到错误协议

                            if (bootPara.isErrorContinue() == false) {
                                if (testDeviceThread != null) {
                                    //testDeviceThread.interrupt();
                                    testDevThreadRunFlag = false;
                                    durTime.cancel();
                                    ThreadUtils.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            binding.imagePlayAndStop.setChecked(false);
                                        }
                                    });
                                }
                            }

                            if (bootPara.isAlarmSound()) {
                                playAlarmSound();
                            }

                            if (!bootPara.isErrorContinue() && testDeviceThread != null) {
                                testDevThreadRunFlag = false;
                                durTime.cancel();
                                binding.imagePlayAndStop.setChecked(false);
                            }
                        }
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
