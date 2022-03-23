package com.industio.uart.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.CollectionUtils;
import com.industio.uart.databinding.ActivityCommunicationDataBinding;
import com.industio.uart.utils.LogFileUtils;

import java.util.List;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == LogFileUtils.REQUESTCODE_FROM_ACTIVITY) {
                List<String> list = data.getStringArrayListExtra("paths");
                if (CollectionUtils.isNotEmpty(list)) {
                    Intent intent = new Intent(ComDataActivity.this,LogViewActivity.class);
                    intent.putExtra("fileName",list.get(0));
                    startActivity(intent);
                }
            }
        }
    }
}