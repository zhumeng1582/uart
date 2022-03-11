package com.industio.uart.utils;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.TimeUtils;

import java.text.SimpleDateFormat;

public class LogFileUtils {
    public static String createDir(String deviceName) {
        SimpleDateFormat simpleDateFormat = com.blankj.utilcode.util.TimeUtils.getSafeDateFormat("yyyyMMddHHmmss");
        String filePath = PathUtils.getAppDataPathExternalFirst();
        if (StringUtils.isEmpty(filePath)) {
            filePath = PathUtils.getExternalAppDataPath();
        }
        if (StringUtils.isEmpty(filePath)) {
            filePath = PathUtils.getDataPath();
        }

        filePath = filePath + "/log";


        if (FileUtils.createOrExistsDir(filePath)) {
            try {
                if (FileUtils.getLength(filePath) > 100 * 1024 * 1024) {
                    FileUtils.deleteAllInDir(filePath);
                }
            } catch (NumberFormatException ignored) {

            }

            filePath = filePath + "/" + deviceName;
        }


        if (FileUtils.createOrExistsDir(filePath)) {
            filePath = filePath + "/" + deviceName + TimeUtils.getNowString(simpleDateFormat) + ".log";
        }
        return filePath;
    }
}
