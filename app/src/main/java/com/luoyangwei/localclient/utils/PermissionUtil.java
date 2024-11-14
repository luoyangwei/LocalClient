package com.luoyangwei.localclient.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PermissionUtil {
    private static final String TAG = PermissionUtil.class.getName();

    public void request(Context context, String... permissions) {
        XXPermissions.with(context)
                .permission(permissions)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (!allGranted) {
                            Log.i(TAG, "获取部分权限成功，但部分权限未正常授予");
                            return;
                        }
                        Log.i(TAG, "权限已授予");
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        if (doNotAskAgain) {
                            Log.e(TAG, "被永久拒绝授权，请手动授予权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(context, permissions);
                        } else {
                            Log.e(TAG, "权限获取权限失败");
                        }
                    }
                });
    }

}
