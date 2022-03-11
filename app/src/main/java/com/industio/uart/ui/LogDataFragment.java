package com.industio.uart.ui;

import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.industio.uart.adapter.LogInfoAdapter;
import com.industio.uart.databinding.FragmentLogDataBinding;
import com.industio.uart.utils.LogFileUtils;

import org.ido.iface.SerialControl;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

public class LogDataFragment extends Fragment {
    private static final String TAG = "LogDataFragment";

    private FragmentLogDataBinding binding;
    private final LogInfoAdapter logInfoAdapter = new LogInfoAdapter();
    private SerialControl mLogSerialControl;
    private String filePath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLogDataBinding.inflate(getLayoutInflater());
        initView();
        return binding.getRoot();

    }

    private void initView() {
        String[] portRate = {"1500000", "115200"};
        ArrayAdapter<String> adapterPortRate = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, portRate);
        adapterPortRate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPortRate.setAdapter(adapterPortRate);
        binding.spinnerPortRate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initLogSerial();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        String[] deviceNo = {"/dev/ttyS0", "/dev/ttyS5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, deviceNo);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPortValue.setAdapter(adapter);
        binding.spinnerPortValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initLogSerial();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        LinearLayoutManager logLinearLayoutManager = new LinearLayoutManager(getContext());
        logLinearLayoutManager.setStackFromEnd(true);
        logLinearLayoutManager.scrollToPositionWithOffset(logInfoAdapter.getItemCount() - 1, Integer.MIN_VALUE);
        binding.recyclerViewLogDetails.setLayoutManager(logLinearLayoutManager);
        binding.recyclerViewLogDetails.setAdapter(logInfoAdapter);
        binding.recyclerViewLogDetails.setLayoutManager(logLinearLayoutManager);
    }

    private void refreshLog() {
//        ThreadUtils.executeByCachedAtFixRate(new ThreadUtils.SimpleTask<Object>() {
//            @Override
//            public Object doInBackground() throws Throwable {
//                count++;
//                return null;
//            }
//
//            @Override
//            public void onSuccess(Object result) {
//                logInfoAdapter.add("" + count);
//            }
//        }, 10, TimeUnit.MILLISECONDS);

        ThreadUtils.executeByCachedAtFixRate(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                return null;
            }

            @Override
            public void onSuccess(Object result) {
                logInfoAdapter.refresh(binding.recyclerViewLogDetails);
            }
        }, 100, TimeUnit.MILLISECONDS);
    }


    //初始化日志串口
    private void initLogSerial() {
        if (mLogSerialControl != null) {
            mLogSerialControl.close();
            mLogSerialControl = null;
        }
        filePath = LogFileUtils.createDir(getDeviceName());

        int portRate = Integer.parseInt(binding.spinnerPortRate.getSelectedItem().toString());
        Log.d(TAG, "portRate:" + portRate);
        logInfoAdapter.clearAll();
        mLogSerialControl = new SerialControl() {
            @Override
            protected void read(final byte[] buf, final int len) {

                ThreadUtils.runOnUiThread(() -> {
                    try {

                        String log = new String(buf, "UTF-8");
                        logInfoAdapter.add(log);

                        if (isSaveLog() && !StringUtils.isEmpty(filePath)) {
                            FileIOUtils.writeFileFromString(filePath, log, true);

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
        refreshLog();
    }

    private boolean isSaveLog() {
        return true;
    }

    private String getDeviceName() {
        return "EVB3568";
    }
}
