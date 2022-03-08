package com.industio.uart.bean;

import com.industio.uart.cache.AccessParaContent;

import java.io.Serializable;

public class BootPara implements Serializable {
    private String deviceName = "EVB3568";
    private AccessPara accessPort = AccessParaContent.accessPara1;
    private boolean shutTimesSwitch = true;
    private int shutTimes = 10;
    private int shutUpDur = 1000;
    private int shutDownDur = 1000;
    private boolean fullShutUp = true;
    private int fullShutUpDur = 10000;
    private boolean errorContinue = true;
    private boolean alarmSound = true;
    private boolean saveLog = true;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public AccessPara getAccessPort() {
        return accessPort;
    }

    public void setAccessPort(AccessPara accessPort) {
        this.accessPort = accessPort;
    }

    public boolean isShutTimesSwitch() {
        return shutTimesSwitch;
    }

    public void setShutTimesSwitch(boolean shutTimesSwitch) {
        this.shutTimesSwitch = shutTimesSwitch;
    }

    public int getShutTimes() {
        return shutTimes;
    }

    public void setShutTimes(int shutTimes) {
        this.shutTimes = shutTimes;
    }

    public int getShutUpDur() {
        return shutUpDur;
    }

    public void setShutUpDur(int shutUpDur) {
        this.shutUpDur = shutUpDur;
    }

    public int getShutDownDur() {
        return shutDownDur;
    }

    public void setShutDownDur(int shutDownDur) {
        this.shutDownDur = shutDownDur;
    }

    public boolean isFullShutUp() {
        return fullShutUp;
    }

    public void setFullShutUp(boolean fullShutUp) {
        this.fullShutUp = fullShutUp;
    }

    public int getFullShutUpDur() {
        return fullShutUpDur;
    }

    public void setFullShutUpDur(int fullShutUpDur) {
        this.fullShutUpDur = fullShutUpDur;
    }

    public boolean isErrorContinue() {
        return errorContinue;
    }

    public void setErrorContinue(boolean errorContinue) {
        this.errorContinue = errorContinue;
    }

    public boolean isAlarmSound() {
        return alarmSound;
    }

    public void setAlarmSound(boolean alarmSound) {
        this.alarmSound = alarmSound;
    }

    public boolean isSaveLog() {
        return saveLog;
    }

    public void setSaveLog(boolean saveLog) {
        this.saveLog = saveLog;
    }
}
