package com.industio.uart.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.CacheDiskUtils;
import com.blankj.utilcode.util.StringUtils;
import com.industio.uart.bean.BootPara;
import com.industio.uart.cache.BootParaInstance;
import com.industio.uart.databinding.ActivityCommunicationDataBinding;
import com.industio.uart.databinding.ActivityOffOnSettingBinding;

public class ShutUpDownActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityOffOnSettingBinding binding;
    private String key;
    private BootPara bootPara;

    public static void startActivity(Activity activity, String key) {
        Intent intent = new Intent(activity, ShutUpDownActivity.class);
        intent.putExtra("key", key);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOffOnSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnSure.setOnClickListener(this);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        if (StringUtils.equals(BootParaInstance.KEY_BOOT_PRAR1, key)) {
            bootPara = BootParaInstance.getInstance().getBootPara1();
        } else {
            bootPara = BootParaInstance.getInstance().getBootPara2();
        }

        binding.textDeviceNameValue.setText(bootPara.getDeviceName());

        binding.rbAccess1.setChecked(bootPara.getAccessPort() == 1);
        binding.rbAccess2.setChecked(bootPara.getAccessPort() == 2);
        binding.rbAccess3.setChecked(bootPara.getAccessPort() == 3);
        binding.rbAccess4.setChecked(bootPara.getAccessPort() == 4);
        binding.rbAccess5.setChecked(bootPara.getAccessPort() == 5);

        binding.cbShutTimesSwitch.setChecked(bootPara.isShutTimesSwitch());
        binding.textShutTimes.setText(""+bootPara.getShutTimes());
        binding.textShoutUpDurValue.setText(""+bootPara.getShutUpDur());
        binding.textShoutDownDurValue.setText(""+bootPara.getShutDownDur());
        binding.cbFullShutUp.setChecked(bootPara.isFullShutUp());
        binding.editFullShutUpDur.setText(""+bootPara.getFullShutUpDur());
        binding.cbErrorContinue.setChecked(bootPara.isErrorContinue());
        binding.cbAlarmSound.setChecked(bootPara.isAlarmSound());
        binding.cbSaveLog.setChecked(bootPara.isSaveLog());


    }

    @Override
    public void onClick(View view) {

        if (view == binding.btnSure) {
            bootPara.setDeviceName(binding.textDeviceNameValue.getText().toString());
            if(binding.rbAccess1.isChecked()){
                bootPara.setAccessPort(1);
            }else if(binding.rbAccess2.isChecked()){
                bootPara.setAccessPort(2);
            }else if(binding.rbAccess3.isChecked()){
                bootPara.setAccessPort(3);
            }else if(binding.rbAccess4.isChecked()){
                bootPara.setAccessPort(4);
            }else if(binding.rbAccess5.isChecked()){
                bootPara.setAccessPort(5);
            }
            bootPara.setShutTimesSwitch(binding.cbShutTimesSwitch.isChecked());

            bootPara.setShutTimes(Integer.parseInt(binding.textShutTimes.getText().toString()));
            bootPara.setShutUpDur(Integer.parseInt(binding.textShoutUpDurValue.getText().toString()));
            bootPara.setShutDownDur(Integer.parseInt(binding.textShoutDownDurValue.getText().toString()));
            bootPara.setFullShutUp(binding.cbFullShutUp.isChecked());
            bootPara.setFullShutUpDur(Integer.parseInt(binding.editFullShutUpDur.getText().toString()));
            bootPara.setErrorContinue(binding.cbErrorContinue.isChecked());
            bootPara.setAlarmSound(binding.cbAlarmSound.isChecked());
            bootPara.setSaveLog(binding.cbSaveLog.isChecked());

            if (StringUtils.equals(BootParaInstance.KEY_BOOT_PRAR1, key)) {
                BootParaInstance.getInstance().saveBootPara1(bootPara);
            } else {
                BootParaInstance.getInstance().saveBootPara2(bootPara);
            }
            finish();
        }
    }
}