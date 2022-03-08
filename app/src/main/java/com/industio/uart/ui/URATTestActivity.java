package com.industio.uart.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ClickUtils;
import com.industio.uart.databinding.ActivityMainBinding;
import com.industio.uart.databinding.ActivityUartTestBinding;

public class URATTestActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityUartTestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUartTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ClickUtils.applySingleDebouncing(new View[]{binding.textStart
        }, this);

        String[] deviceNo = {"/dev/ttyS1", "/dev/ttyS2", "/dev/ttyS3", "/dev/ttyS4"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, deviceNo);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDeviceNo.setAdapter(adapter);

        String[] portRate = {"115200", "1500000"};
        ArrayAdapter<String> adapterPortRate = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, portRate);
        adapterPortRate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPortRate.setAdapter(adapterPortRate);

        String[] checkBit = {"奇校验", "偶校验"};
        ArrayAdapter<String> adapterCheckBit = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, checkBit);
        adapterCheckBit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCheckBit.setAdapter(adapterCheckBit);


        String[] type = {"接收", "发送"};
        ArrayAdapter<String> adapterType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, type);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerType.setAdapter(adapterType);

        initView();
    }

    private void initView() {
        binding.imageBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.imageBack) {
            finish();
        }
    }
}