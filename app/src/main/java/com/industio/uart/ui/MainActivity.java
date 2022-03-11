package com.industio.uart.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.industio.uart.databinding.ActivityMainBinding;
import com.industio.uart.utils.DataProtocol;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ClickUtils.applySingleDebouncing(new View[]{
                binding.btnAutoTest,
                binding.btnUARTTest,
                binding.btnETHTest,
                binding.btnCANTest
        }, this);

    }

    @Override
    public void onClick(View view) {
        PermissionUtils.permission(PermissionConstants.STORAGE).callback(new PermissionUtils.SimpleCallback() {
            @Override
            public void onGranted() {
                if (view.getId() == binding.btnAutoTest.getId()) {
                    startActivity(new Intent(MainActivity.this, ComDataActivity.class));
                } else if (view.getId() == binding.btnUARTTest.getId()) {
                    startActivity(new Intent(MainActivity.this, URATTestActivity.class));
                } else if (view.getId() == binding.btnETHTest.getId()) {

                } else if (view.getId() == binding.btnCANTest.getId()) {
                }
            }

            @Override
            public void onDenied() {

            }
        }).request();

    }

}