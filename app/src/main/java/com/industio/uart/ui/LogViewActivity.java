package com.industio.uart.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.industio.uart.databinding.ActivityUartTestBinding;
import com.industio.uart.databinding.ActivityViewLogDataBinding;

public class LogViewActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityViewLogDataBinding binding;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityViewLogDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        fileName = getIntent().getStringExtra("fileName");
        binding.textLogName.setText(FileUtils.getFileName(fileName));
        binding.textLog.setText(FileIOUtils.readFile2String(fileName));
        binding.imageBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.imageBack) {
            finish();
        }
    }
}