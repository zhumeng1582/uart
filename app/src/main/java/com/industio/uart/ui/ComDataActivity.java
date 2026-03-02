package com.industio.uart.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.industio.uart.databinding.ActivityCommunicationDataBinding;

public class ComDataActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityCommunicationDataBinding binding;

    private final ActivityResultLauncher<String[]> openDocumentLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            uri -> {
                if (uri != null) {
                    Intent intent = new Intent(ComDataActivity.this, LogViewActivity.class);
                    intent.putExtra("fileUri", uri.toString());
                    startActivity(intent);
                }
            }
    );

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
            // 使用系统文档选择器，支持 .log .txt .json
            openDocumentLauncher.launch(new String[]{
                    "text/plain",
                    "application/json",
                    "*/*"
            });
        }
    }
}