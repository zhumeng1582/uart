package com.industio.uart.ui;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
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
    private SerialControl mLogSerialControl;
    private int testCount = 0;
    private int errorCount = 0;
    private long testTimeLong = 0;
    private int uartRxDataFlag = 0;
    private static int openLogUartCnt = 0;

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

        binding.textTerminal.setText("控制台" + getTag());
        binding.imageSetting.setOnClickListener(this);
        binding.imagePlayAndStop.setOnClickListener(this);

        String[] deviceNo = {"/dev/ttyS0", "/dev/ttyS5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, deviceNo);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPortValue.setAdapter(adapter);
        binding.spinnerPortValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                openLogUartCnt++;
                if (openLogUartCnt == 1)
                    initLogSerial();

                if (openLogUartCnt > 4)
                    initLogSerial();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e(TAG, "========>" + openLogUartCnt);
            }
        });

        binding.textErrorDetails.setMovementMethod(ScrollingMovementMethod.getInstance());
        binding.textLogDetails.setMovementMethod(ScrollingMovementMethod.getInstance());

        String[] portRate = {"1500000", "115200"};
        ArrayAdapter<String> adapterPortRate = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, portRate);
        adapterPortRate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPortRate.setAdapter(adapterPortRate);
        binding.spinnerPortRate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                openLogUartCnt++;
                if (openLogUartCnt == 1)
                    initLogSerial();

                if (openLogUartCnt > 4)
                    initLogSerial();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e(TAG, "========>" + openLogUartCnt);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        //设置数据后需要刷新页面计数信息
        binding.textTotalTestTimesValue.setText(bootPara.getTestCount() + "");
        binding.textTotalTestErrorTimesValue.setText(bootPara.getErrorCount() + "");
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
            openLogUartCnt = 0;
        } else if (v == binding.imagePlayAndStop) {
            if (binding.imagePlayAndStop.isChecked()) {
                binding.imagePlayAndStop.setChecked(false);
                durTime.cancel();
            } else {
                binding.imagePlayAndStop.setChecked(true);
                testDevice();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        binding.imagePlayAndStop.setChecked(false);

        if (durTime != null && !durTime.isCanceled()) {
            durTime.cancel();

        }
        if (testDeviceThread != null && testDeviceThread.isInterrupted()) {
            testDeviceThread.interrupt();
        }

        //将计数信息保存
        if (StringUtils.equals(getTag(), "1")) {
            BootParaInstance.getInstance().saveBootPara1(bootPara);
        } else {
            BootParaInstance.getInstance().saveBootPara2(bootPara);

        }
    }

    private void testDevice() {
        clearCount();

        initErrorInfoSerial();

        testDeviceThread = new Thread(new Runnable() {
            int timeCnt;

            @Override
            public void run() {
                while (binding.imagePlayAndStop.isChecked()) {
                    Log.e(TAG, "testDeviceThread running...");
                    try {
                        uartRxDataFlag = 0;
                        timeCnt = 0;

                        if (bootPara.isShutTimesSwitch()) {   //电源波动
                            for (int i = 0; i < bootPara.getShutTimes(); i++) {
                                setPower(true);
                                Thread.sleep(bootPara.getShutUpDur());
                                setPower(false);
                                Thread.sleep(bootPara.getShutDownDur());
                                if (!binding.imagePlayAndStop.isChecked()) {//被手动停止
                                    break;
                                }
                            }
                        }

                        ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                testCount++;
                                bootPara.setTestCount(bootPara.getTestCount() + 1);
                                binding.textTestTimesValue.setText(testCount + "");
                                binding.textTotalTestTimesValue.setText(bootPara.getTestCount() + "");
                            }
                        });

                        //持续上电
                        setPower(true);

                        if (bootPara.isFullShutUp()) {
                            timeCnt = bootPara.getFullShutUpDur();
                            while (timeCnt-- > 0) {
                                Thread.sleep(1000);
                                if (!binding.imagePlayAndStop.isChecked()) {//被手动停止
                                    timeCnt = 0;
                                    break;
                                }
                            }

                            if (binding.imagePlayAndStop.isChecked() && uartRxDataFlag == 0) {//超时判断
                                if (binding.imagePlayAndStop.isChecked() && bootPara.isAlarmSound()) {
                                    playAlarmSound();
                                }

                                ThreadUtils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        errorCount++;
                                        if (bootPara.isErrorContinue() == false) {
                                            durTime.cancel();
                                            binding.imagePlayAndStop.setChecked(false);
                                        }

                                        binding.textErrorDetails.setText("超时错误！\n");
                                        bootPara.setErrorCount(bootPara.getErrorCount() + 1);
                                        binding.textTestErrorTimesValue.setText(errorCount + "");
                                        binding.textTotalTestErrorTimesValue.setText(bootPara.getErrorCount() + "");
                                    }
                                });
                                if (bootPara.isErrorContinue() == false)
                                    Thread.sleep(200);
                            } else if (!binding.imagePlayAndStop.isChecked()) {//被手动停止
                                break;
                            }
                        } else {//非足额上电
                            timeCnt = 0;
                            while (uartRxDataFlag == 0) {
                                timeCnt++;

                                if (timeCnt >= bootPara.getFullShutUpDur() && uartRxDataFlag == 0) {//超时判断
                                    if (binding.imagePlayAndStop.isChecked() && bootPara.isAlarmSound()) {
                                        playAlarmSound();
                                    }

                                    ThreadUtils.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (bootPara.isErrorContinue() == false) {
                                                durTime.cancel();
                                                binding.imagePlayAndStop.setChecked(false);
                                            }

                                            errorCount++;
                                            binding.textErrorDetails.setText("超时错误！\n");
                                            bootPara.setErrorCount(bootPara.getErrorCount() + 1);
                                            binding.textTestErrorTimesValue.setText(errorCount + "");
                                            binding.textTotalTestErrorTimesValue.setText(bootPara.getErrorCount() + "");
                                        }
                                    });
                                    uartRxDataFlag = -1;
                                } else if (!binding.imagePlayAndStop.isChecked()) {//被手动停止
                                    uartRxDataFlag = -1;
                                    // break;
                                }
                                Thread.sleep(1000);
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
        binding.textTestTimesValue.setText(testCount + "");
        errorCount = 0;
        binding.textTestErrorTimesValue.setText(errorCount + "");
        testTimeLong = 0;
        binding.textTestDurationValue.setText(com.industio.uart.utils.TimeUtils.getDurTime(testTimeLong));
    }


    //初始化错误串口
    private void initErrorInfoSerial() {

        binding.textErrorDetails.setText("");

        SerialControl mErrLogSerialControl = new SerialControl() {
            @Override
            protected void read(byte[] buf, int len) {
                ThreadUtils.runOnUiThread(() -> {
                    Log.d(TAG, "ErrorInfoSerial rx:" + len + "," + bytesToHexString(buf, len));
                    if ((buf[0] & 0xFF) == DataProtocol.START_FRAME && (buf[3] & 0xFF) == DataProtocol.END_FRAME) {
                        if ((buf[1] & 0xFF) == DataProtocol.OK) {
                            uartRxDataFlag = 2;//接收到OK协议
                            ThreadUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.textErrorDetails.setText("测试通过！\n");
                                }
                            });
                        } else if ((buf[1] & 0xFF) != DataProtocol.OK) {//收到错误码
                            Log.d(TAG, "err codec:" + bytesToHexString(buf, len));
                            ThreadUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (binding.imagePlayAndStop.isChecked()) {
                                        errorCount++;
                                        bootPara.setErrorCount(bootPara.getErrorCount() + 1);
                                    }

                                    binding.textTestErrorTimesValue.setText(errorCount + "");
                                    binding.textTotalTestErrorTimesValue.setText(bootPara.getErrorCount() + "");

                                    String errorText = DataAnalysis.analysis(buf[1] & 0xFF, buf[2] & 0xFF) + "\n";
                                    if (binding.textErrorDetails.getText().toString().contains("测试通过"))
                                        binding.textErrorDetails.setText("");

                                    if (binding.textErrorDetails.getText().length() > 80) //内容过多清屏
                                        binding.textErrorDetails.setText("");

                                    binding.textErrorDetails.setText(binding.textErrorDetails.getText().toString() + errorText);
                                }
                            });

                            uartRxDataFlag = 1;//接收到错误协议

                            if (binding.imagePlayAndStop.isChecked() && bootPara.isAlarmSound())
                                playAlarmSound();


                            if (bootPara.isErrorContinue() == false && testDeviceThread != null) {
                                durTime.cancel();
                                ThreadUtils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.imagePlayAndStop.setChecked(false);
                                    }
                                });

                                if (bootPara.isErrorContinue() == false)
                                    try {
                                        Thread.sleep(100);//等待binding.imagePlayAndStop.setChecked(false);更新
                                    } catch (Exception e) {
                                    }
                            }
                        }
                    } else {
                        Log.d(TAG, "not process err code:" + bytesToHexString(buf, len));
                    }

                });
            }
        };

        String portName = bootPara.getAccessPort().getPort();
        if (mErrLogSerialControl.init(portName, 9600, 8, 'N', 1, 0, 10)) {
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
        if (mLogSerialControl != null) {
            mLogSerialControl.close();
            mLogSerialControl = null;
        }
        int portRate = Integer.parseInt(binding.spinnerPortRate.getSelectedItem().toString());
        Log.d(TAG, "portRate:" + portRate);
        binding.textLogDetails.setText("");
        mLogSerialControl = new SerialControl() {
            @Override
            protected void read(final byte[] buf, final int len) {

                ThreadUtils.runOnUiThread(() -> {
                    try {
                        String log = new String(buf, "UTF-8") + "\n";
                        binding.textLogDetails.setText(binding.textLogDetails.getText() + log);
                        if (bootPara.isSaveLog()) {
                            SimpleDateFormat simpleDateFormat = TimeUtils.getSafeDateFormat("yyyyMMddHHmmss");
                            String filePath = PathUtils.getAppDataPathExternalFirst();
                            if (StringUtils.isEmpty(filePath)) {
                                filePath = PathUtils.getExternalAppDataPath();
                            }
                            if (StringUtils.isEmpty(filePath)) {
                                filePath = PathUtils.getDataPath();
                            }

                            filePath = filePath + "/test";
                            if (FileUtils.createOrExistsDir(filePath)) {
                                filePath = filePath + "/" + bootPara.getDeviceName();
                                if (FileUtils.createOrExistsDir(filePath)) {
                                    String fileName = filePath + "/" + bootPara.getDeviceName() + TimeUtils.getNowString(simpleDateFormat) + ".log";
                                    FileIOUtils.writeFileFromString(fileName, log, true);
                                }
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                });
            }
        };
        String portName = (String) binding.spinnerPortValue.getSelectedItem();
        if (mLogSerialControl.init(portName, portRate, 8, 'N', 1, 0, 0)) {
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
            // mpLeft.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mpLeft.setDataSource(fdLeft.getFileDescriptor(), fdLeft.getStartOffset(), fdLeft.getLength());
            mpLeft.prepare();
            mpLeft.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String bytesToHexString(byte[] src, int len) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < len; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }

}
