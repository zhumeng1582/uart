package com.industio.uart.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.industio.uart.databinding.ActivityCommunicationDataBinding;

public class ComDataActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityCommunicationDataBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCommunicationDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.imageBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == binding.imageBack) {
            finish();
        }
    }
}