package com.industio.uart.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.industio.uart.adapter.LogInfoAdapter;
import com.industio.uart.databinding.ActivityCommunicationDataBinding;
import com.industio.uart.utils.LogFileUtils;

import org.ido.iface.SerialControl;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

public class ComDataActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityCommunicationDataBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCommunicationDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.imageBack.setOnClickListener(this);
        binding.textLog.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        if (view == binding.imageBack) {
            finish();
        } else if (view == binding.textLog) {
            LogFileUtils.openLogFileFolder(this);
        }
    }


}