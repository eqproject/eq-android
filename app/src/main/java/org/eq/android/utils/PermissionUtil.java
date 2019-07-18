package org.eq.android.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import org.eq.android.R;
import org.eq.android.activity.BaseActivity;

import static android.Manifest.permission;


/**
 * 动态权限工具类
 *
 */

public class PermissionUtil {

    public static final int REQUEST_TO_APPLY_PERMISSION = 1234;
    public static final int REQUEST_TO_REFUSE_PERMISSION = 1;

    //拍照权限
    public static final String[] PER_CAMERA = {permission.CAMERA, permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE};

    //从相册选照片权限
    public static final String[] PER_SELECT_PHOTO = {permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE};

    /**
     * 申请权限
     *
     * @param activity
     * @param permissions
     * @param requestCode
     * @return 是否有权限
     */
    public static boolean applyPermission(Activity activity, String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            for (int i = 0; i < permissions.length; i++) {
                int result = ContextCompat.checkSelfPermission(activity, permissions[i]);
                // 权限是否已经授权 GRANTED---授权  DINIED---拒绝
                if (result != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, permissions, requestCode);
                    return false;
                }
            }

        }
        return true;
    }

    /**
     * 检测是否开启权限成功
     *
     * @param activity
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public static boolean checkIsOpenPermission(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_TO_APPLY_PERMISSION && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                boolean isCanApply = activity.shouldShowRequestPermissionRationale(permissions[0]);
                if (!isCanApply) {
                    //去手动设置权限
//                    BaseJumpUtil.jumpToPermissionSetting(activity);
                    return false;
                }
            }
        }else if (requestCode == 1){
            return false;
        }
        return true;
    }

/*    *//**
     * 跳转到应用设置界面提示用户手动开启权限
     *
     * @param activity
     *//*
    public static void jumpToPermissionSetting(Activity activity) {
        if (activity instanceof BaseActivity)
            ((BaseActivity) activity).showToast(R.string.open_permission);
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, PermissionUtil.REQUEST_TO_APPLY_PERMISSION + 1);
    }

    *//**
     * 弹出Toast
     *
     * @param resId
     *//*
    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }*/
}
