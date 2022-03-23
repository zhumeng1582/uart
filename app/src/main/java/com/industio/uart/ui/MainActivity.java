package com.industio.uart.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.industio.uart.databinding.ActivityMainBinding;
import com.industio.uart.utils.DataProtocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding binding;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIdoCheck() == false) {
            Toast.makeText(this, "授权失败！！！", Toast.LENGTH_LONG).show();
            this.finish();
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ClickUtils.applySingleDebouncing(new View[]{
                binding.btnAutoTest,
                binding.btnUARTTest,
                binding.btnETHTest,
                binding.btnCANTest
        }, this);

        mContext = this;
    }

    @Override
    public void onClick(View view) {
        PermissionUtils.permission(PermissionConstants.STORAGE).callback(new PermissionUtils.SimpleCallback() {
            @Override
            public void onGranted() {
                if (view.getId() == binding.btnAutoTest.getId()) {
                    startActivity(new Intent(MainActivity.this, ComDataActivity.class));
                } else if (view.getId() == binding.btnUARTTest.getId()) {
                    if(getIdoCheck() == true) {
                        startActivity(new Intent(MainActivity.this, URATTestActivity.class));
                    } else {
                        Toast.makeText(mContext, "授权失败！！！", Toast.LENGTH_LONG).show();
                    }
                } else if (view.getId() == binding.btnETHTest.getId()) {

                } else if (view.getId() == binding.btnCANTest.getId()) {
                }
            }

            @Override
            public void onDenied() {

            }
        }).request();
    }

    public static boolean getIdoCheck() {
        byte[] buff = new byte[5];
        final String IDO_CHECK_NODE = "/dev/ido_zc";

        if (new File(IDO_CHECK_NODE).exists()) {
            FileWriter writer = null;
            try {
                writer = new FileWriter(IDO_CHECK_NODE);
                writer.write("9DOI");
                writer.flush();
                writer.close();
                FileInputStream checkRead = new FileInputStream(new File(IDO_CHECK_NODE));
                if (checkRead.read(buff, 0, 5) == 5) {
                    if((buff[0]&0xff) == 0x25 &&(buff[1]&0xff) == 0xf5 && (buff[2]&0xff) == 0x95 && (buff[4]&0xff) == 0) {
                        //Log.e("ido", "ido check ok!!!");
                        return true;
                    } else {
                        //Log.e("ido", "ido check failed!!!");
                    }
                } else {
                    //Log.e("ido", "ido check failed!");
                }
                checkRead.close();
            } catch (Exception ex) {
                Log.e("ido", "" + ex);
            }
        }
        return false;
    }
}