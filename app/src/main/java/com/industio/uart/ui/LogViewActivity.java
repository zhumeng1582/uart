package com.industio.uart.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.industio.uart.databinding.ActivityViewLogDataBinding;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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
        String fileUriStr = getIntent().getStringExtra("fileUri");
        if (fileUriStr != null) {
            Uri uri = Uri.parse(fileUriStr);
            binding.textLogName.setText(getDisplayName(uri));
            String content = readUriToString(uri);
            binding.textLog.setText(content != null ? content : "");
        } else {
            fileName = getIntent().getStringExtra("fileName");
            if (fileName != null) {
                binding.textLogName.setText(FileUtils.getFileName(fileName));
                binding.textLog.setText(FileIOUtils.readFile2String(fileName));
            }
        }
        binding.imageBack.setOnClickListener(this);
    }

    private String getDisplayName(Uri uri) {
        String name = uri.getLastPathSegment();
        return name != null ? name : "已选文件";
    }

    private String readUriToString(Uri uri) {
        try (InputStream is = getContentResolver().openInputStream(uri)) {
            if (is == null) return null;
            byte[] buf = new byte[1024];
            StringBuilder sb = new StringBuilder();
            int n;
            while ((n = is.read(buf)) != -1) {
                sb.append(new String(buf, 0, n, StandardCharsets.UTF_8));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onClick(View view) {
        if (view == binding.imageBack) {
            finish();
        }
    }
}