package com.industio.uart.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.TimeUtils;

import java.text.SimpleDateFormat;

public class LogFileUtils {
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

        SimpleDateFormat simpleDateFormat = com.blankj.utilcode.util.TimeUtils.getSafeDateFormat("MMddHHmmss"/*yyyyMMddHHmmss*/);

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
        if (FileUtils.createOrExistsDir(fileDir)) {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", FileUtils.getFileByPath(fileDir));
            intent.setData(uri);
            try {
                activity.startActivity(intent);
//            startActivity(Intent.createChooser(intent,"选择浏览工具"));
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }

        }



    }



}
