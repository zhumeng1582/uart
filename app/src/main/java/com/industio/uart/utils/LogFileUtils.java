package com.industio.uart.utils;

import android.app.Activity;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.leon.lfilepickerlibrary.LFilePicker;

import java.text.SimpleDateFormat;

public class LogFileUtils {
    public static int REQUESTCODE_FROM_ACTIVITY = 1000;

    public static String createDir(String deviceName) {
        String filePath = getLogFileDir();

        if (FileUtils.createOrExistsDir(filePath)) {
            try {
                if (FileUtils.getLength(filePath) > 100 * 1024 * 1024) {
                    FileUtils.deleteAllInDir(filePath);
                }
            } catch (NumberFormatException ignored) {
            }

            filePath = filePath + "/" + deviceName;
        }

        SimpleDateFormat simpleDateFormat = TimeUtils.getSafeDateFormat("MMddHHmmss"/*yyyyMMddHHmmss*/);

        if (FileUtils.createOrExistsDir(filePath)) {
            filePath = filePath + "/" + deviceName + "_" + TimeUtils.getNowString(simpleDateFormat) + ".log";
        }
        return filePath;
    }

    public static String getJsonFile() {
        String filePath = PathUtils.getAppDataPathExternalFirst();
        if (StringUtils.isEmpty(filePath)) {
            filePath = PathUtils.getExternalAppDataPath();
        }
        if (StringUtils.isEmpty(filePath)) {
            filePath = PathUtils.getDataPath();
        }

        filePath = filePath + "/json/cmd.json";
        FileUtils.createOrExistsFile(filePath);
        return filePath;
    }

    private static String getLogFileDir() {
        String filePath = PathUtils.getAppDataPathExternalFirst();
        if (StringUtils.isEmpty(filePath)) {
            filePath = PathUtils.getExternalAppDataPath();
        }
        if (StringUtils.isEmpty(filePath)) {
            filePath = PathUtils.getDataPath();
        }

        filePath = filePath + "/logs";
        FileUtils.createOrExistsDir(filePath);
        return filePath;
    }

    public static void openLogFileFolder(Activity activity) {
        String fileDir = getLogFileDir();

        new LFilePicker()
                .withActivity(activity)
                .withBackgroundColor("#666666")
                .withRequestCode(REQUESTCODE_FROM_ACTIVITY)
                .withStartPath(fileDir)//指定初始显示路径
                .withIsGreater(false)//过滤文件大小 小于指定大小的文件
                .withFileFilter(new String[]{".log", ".txt", ".json"})
//                .withFileSize(500 * 1024)//指定文件大小为500K
                .withMutilyMode(false)
                .start();

    }
}
