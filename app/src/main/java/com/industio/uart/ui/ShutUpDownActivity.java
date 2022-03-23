package com.industio.uart.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.StringUtils;
import com.industio.uart.bean.BootPara;
import com.industio.uart.cache.AccessParaContent;
import com.industio.uart.cache.AccessParaContent1;
import com.industio.uart.cache.AccessParaContent2;
import com.industio.uart.cache.BootParaInstance;
import com.industio.uart.databinding.ActivityOffOnSettingBinding;

public class ShutUpDownActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityOffOnSettingBinding binding;
    private String key;
    private BootPara bootPara;
    private AccessParaContent accessParaContent;

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
        binding.imageBack.setOnClickListener(this);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        if (StringUtils.equals(BootParaInstance.KEY_BOOT_PRAR1, key)) {
            bootPara = BootParaInstance.getInstance().getBootPara1();
            accessParaContent = new AccessParaContent1();
        } else {
            bootPara = BootParaInstance.getInstance().getBootPara2();
            accessParaContent = new AccessParaContent2();
        }

        binding.textDeviceNameValue.setText(bootPara.getDeviceName());

        binding.rbAccess1.setText(accessParaContent.accessPara1().getName());
        binding.rbAccess2.setText(accessParaContent.accessPara2().getName());
        binding.rbAccess3.setText(accessParaContent.accessPara3().getName());
        binding.rbAccess4.setText(accessParaContent.accessPara4().getName());
        binding.rbAccess5.setText(accessParaContent.accessPara5().getName());

        binding.rbAccess1.setChecked(StringUtils.equals(bootPara.getAccessPort().getName(),binding.rbAccess1.getText().toString()));
        binding.rbAccess2.setChecked(StringUtils.equals(bootPara.getAccessPort().getName(),binding.rbAccess2.getText().toString()));
        binding.rbAccess3.setChecked(StringUtils.equals(bootPara.getAccessPort().getName(),binding.rbAccess3.getText().toString()));
        binding.rbAccess4.setChecked(StringUtils.equals(bootPara.getAccessPort().getName(),binding.rbAccess4.getText().toString()));
        binding.rbAccess5.setChecked(StringUtils.equals(bootPara.getAccessPort().getName(),binding.rbAccess5.getText().toString()));

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
                bootPara.setAccessPort(accessParaContent.accessPara1());
            }else if(binding.rbAccess2.isChecked()){
                bootPara.setAccessPort(accessParaContent.accessPara2());
            }else if(binding.rbAccess3.isChecked()){
                bootPara.setAccessPort(accessParaContent.accessPara3());
            }else if(binding.rbAccess4.isChecked()){
                bootPara.setAccessPort(accessParaContent.accessPara4());
            }else if(binding.rbAccess5.isChecked()){
                bootPara.setAccessPort(accessParaContent.accessPara5());
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

            bootPara.setTestTimeLong(0);
            bootPara.setTestCount(0);
            bootPara.setErrorCount(0);

            if (StringUtils.equals(BootParaInstance.KEY_BOOT_PRAR1, key)) {
                BootParaInstance.getInstance().saveBootPara1(bootPara);
            } else {
                BootParaInstance.getInstance().saveBootPara2(bootPara);
            }
            finish();
        }else if(view == binding.imageBack){
            finish();
        }
    }
}