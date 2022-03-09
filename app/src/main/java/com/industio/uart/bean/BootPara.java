package com.industio.uart.bean;


import java.io.Serializable;

public class BootPara implements Serializable {
    private String deviceName = "EVB3568";
    private AccessPara accessPort;

    public BootPara(AccessPara accessPort) {
        this.accessPort = accessPort;
        shutTimesSwitch = true;
        shutTimes = 5;
        shutUpDur = 1000;
        shutDownDur = 1000;
        fullShutUp = true;
        fullShutUpDur = 60;
        errorContinue = false;
        alarmSound = true;
        saveLog = true;
        testCount = 0;
        errorCount = 0;
        testTimeLong = 0;
    }

    private boolean shutTimesSwitch;
    private int shutTimes;
    private int shutUpDur;
    private int shutDownDur;
    private boolean fullShutUp;
    private int fullShutUpDur;
    private boolean errorContinue;
    private boolean alarmSound;
    private boolean saveLog;
    private int testCount;
    private int errorCount;
    private long testTimeLong;

    public int getTestCount() {
        return testCount;
    }

    public void setTestCount(int testCount) {
        this.testCount = testCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public long getTestTimeLong() {
        return testTimeLong;
    }

    public void setTestTimeLong(long testTimeLong) {
        this.testTimeLong = testTimeLong;
    }

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
