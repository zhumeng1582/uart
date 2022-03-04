package com.industio.uart.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.industio.uart.databinding.ActivityCommunicationDataBinding;
import com.industio.uart.databinding.ActivityOffOnSettingBinding;

public class ShutUpDownActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityOffOnSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOffOnSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

    @Override
    public void onClick(View view){

    }
}