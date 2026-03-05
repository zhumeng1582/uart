package com.industio.uart.utils;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

/**
 * 简单权限申请工具，使用固定 requestCode（16 位内）避免与系统限制冲突。
 * Activity 需在 onRequestPermissionsResult 中调用 onRequestPermissionsResult。
 */
public class PermissionHelper {

    /** 存储权限请求码，必须 ≤ 0xFFFF */
    public static final int REQUEST_CODE_STORAGE = 0x1000;

    public interface Callback {
        void onGranted();
        void onDenied();
    }

    private static final String[] STORAGE_PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private final FragmentActivity activity;
    private Callback pendingCallback;

    public PermissionHelper(FragmentActivity activity) {
        this.activity = activity;
    }

    /**
     * 申请存储权限；若已全部授予则直接回调 onGranted。
     */
    public void requestStorage(Callback callback) {
        if (callback == null) return;
        boolean needRequest = false;
        for (String p : STORAGE_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(activity, p) != PackageManager.PERMISSION_GRANTED) {
                needRequest = true;
                break;
            }
        }
        if (!needRequest) {
            callback.onGranted();
            return;
        }
        pendingCallback = callback;
        ActivityCompat.requestPermissions(activity, STORAGE_PERMISSIONS, REQUEST_CODE_STORAGE);
    }

    /**
     * 在 Activity 的 onRequestPermissionsResult 中调用，用于接收权限结果。
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != REQUEST_CODE_STORAGE || pendingCallback == null) return;
        boolean allGranted = true;
        if (grantResults != null) {
            for (int r : grantResults) {
                if (r != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
        } else {
            allGranted = false;
        }
        if (allGranted) {
            pendingCallback.onGranted();
        } else {
            pendingCallback.onDenied();
        }
        pendingCallback = null;
    }
}
