package org.eq.android.utils;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import org.eq.android.common.Constant;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


/**
 * 系统基类
 *
 * @author sugy
 *         2016/7/15
 */
public class BaseSystem {

    /**
     * 基础目录
     */
    public static final String DIR_MAIN = "Krfamily";

    /**
     * 存放下载的启动页
     */
    public static final String DIR_LAUNCH = "launch";

    /**
     * 存放图片
     */
    public static final String DIR_IMG = "img";

    /**
     * 存放文件
     */
    public static final String DIR_FILE = "file";

    /**
     * 存放文件
     */
    public static final String DIR_DOWNLOAD = "download";

    public static final int PHOTO = 100;

    public static final int CAMERA = 101;

    public static final int CROP_CAMERA = 102;

    public static final int CROP_PHOTO = 103;

    public static final int FILE_CHOOSE = 104;

    public static final int IMG_CHOOSE = 105;

    public static final int NICKNAME = 106;

    /**
     * sd卡是否存在
     *
     * @return
     */
    public static boolean sdCardExists() {
        return Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取应用主目录路径
     *
     * @return
     */
    public static String getMainPath() {
        if (!sdCardExists()) {
            return "";
        }
        String path = Environment.getExternalStorageDirectory() + File.separator + DIR_MAIN;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 获取应用主目录路径
     *
     * @return
     */
    public static String getMainDownLoadPath() {
        if (!sdCardExists()) {
            return "";
        }
        String path = Environment.getExternalStorageDirectory() + File.separator + "kerui";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 获取图片目录
     *
     * @return
     */
    public static String getImgPath() {
        if (!sdCardExists()) {
            return "";
        }

        String path = getMainPath() + File.separator + DIR_IMG;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 获取存放文件的目录
     *
     * @return
     */
    public static String getDownloadFilePath() {
        if (!sdCardExists()) {
            return "";
        }

        String path = getMainDownLoadPath() + File.separator + DIR_DOWNLOAD;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 调用系统相机
     *
     * @param activity
     */
    public static void jumpToSysCamera(Activity activity, File tempFile) {
        try{
            Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // 指定调用相机拍照后照片的储存路径
            cameraintent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
            activity.startActivityForResult(cameraintent, CAMERA);
        }catch (SecurityException e){
            e.printStackTrace();
        }

    }

    /**
     * 跳转到系统相册
     *
     * @param activity
     */
    public static void jumpToSysAlbum(Activity activity) {
        try {
//            Intent intent = new Intent(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT ? Intent.ACTION_GET_CONTENT : Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.setType("image/*");
//            activity.startActivityForResult(intent, PHOTO);

            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activity.startActivityForResult(intent, PHOTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取真实路径
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getRealPathFromURI(Context context, Uri uri) {
        String photoPath = "";
        if (context == null || uri == null) {
            return photoPath;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if (isExternalStorageDocument(uri)) {
                String[] split = docId.split(":");
                if (split.length >= 2) {
                    String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        photoPath = Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
            } else if (isDownloadsDocument(uri)) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                photoPath = getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                String[] split = docId.split(":");
                if (split.length >= 2) {
                    String type = split[0];
                    Uri contentUris = null;
                    if ("image".equals(type)) {
                        contentUris = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUris = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUris = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    String selection = MediaStore.Images.Media._ID + "=?";
                    String[] selectionArgs = new String[]{split[1]};
                    photoPath = getDataColumn(context, contentUris, selection, selectionArgs);
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            photoPath = uri.getPath();
        } else {
            photoPath = getDataColumn(context, uri, null, null);
        }

        return photoPath;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } catch (RuntimeException e){
                e.printStackTrace();
        }  finally{
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return null;
    }

    /**
     * 使用系统当前日期加以调整作为照片的名称
     *
     * @return
     */
    public static String getPhotoFileName() {
        Random random = new Random();
        return getPhotoFileName(String.valueOf(random.nextInt(10000)));
    }

    /**
     * 使用系统当前日期加以调整作为照片的名称(png格式)
     *
     * @return
     */
    public static String getPhotoFileNameAsPng() {
        Random random = new Random();
        return getPhotoFileName(String.valueOf(random.nextInt(10000)), "png");
    }

    /**
     * 使用系统当前日期加以调整作为照片的名称
     *
     * @param oldFileName
     * @return
     */
    public static String getPhotoFileName(String oldFileName) {
        return getPhotoFileName(oldFileName, "jpg");
    }

    /**
     * 使用系统当前日期加以调整作为照片的名称
     *
     * @param oldFileName
     * @param format
     * @return
     */
    public static String getPhotoFileName(String oldFileName, String format) {
        if (!TextUtils.isEmpty(oldFileName)) {
            int index = oldFileName.lastIndexOf("/");
            int indexEnd = oldFileName.lastIndexOf(".");
            oldFileName = oldFileName.substring(index + 1, indexEnd == -1 ? oldFileName.length() : indexEnd);
        }
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss_");
        return dateFormat.format(date) + oldFileName + "." + format;
    }

    /**
     * 跳转到裁剪照片
     *
     * @param activity
     * @param uri
     */
    public static Uri jumpToCropPhoto(Activity activity, Uri uri,
                                      int requestCode) {
        Intent innerIntent = new Intent("com.android.camera.action.CROP");
        innerIntent.setDataAndType(uri, "image/*");
        innerIntent.putExtra("crop", "true");// 才能出剪辑的小方框，不然没有剪辑功能，只能选取图片
        innerIntent.putExtra("aspectX", 1); // 放大缩小比例的X
        innerIntent.putExtra("aspectY", 1);// 放大缩小比例的X   这里的比例为：   1:1
        innerIntent.putExtra("scale", true);
//        innerIntent.putExtra("return-data", true);//部分手机用此方法会崩溃，故用下面的方法

        //uritempFile为Uri类变量，实例化uritempFile
        Uri uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + DIR_MAIN + "-avatar_crop.jpg");
        innerIntent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        innerIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        activity.startActivityForResult(innerIntent, requestCode);
        return uritempFile;
    }

    public final static String[] file_Types = {"pdf", "doc", "docx", "wps", "ppt", "txt", "pptx", "xls", "xlsx"};

    /**
     * 选择文件
     *
     * @param activity
     */
    public static void jumpToSelectFile(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            activity.startActivityForResult(Intent.createChooser(intent, "选择文件"), FILE_CHOOSE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, "找不到文件选择器", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取文件后缀
     *
     * @param path
     * @return
     */
    public static String getFileDot(String path) {
        int lastDot = path.lastIndexOf(".");
        if (lastDot < 0)
            return null;
        String dot = path.substring(lastDot + 1).toLowerCase();
        return dot;
    }


    /**
     * 检查文件大小
     *
     * @param mediaFile
     * @return
     */
    public static long chekcMediaFileSize(File mediaFile, boolean isImg) {

        /** Get length of file in bytes */
        long fileSizeInBytes = mediaFile.length();

        /** Convert the bytes to Kilobytes (1 KB = 1024 Bytes) */
        long fileSizeInKB = fileSizeInBytes / 1024;

        /** Convert the KB to MegaBytes (1 MB = 1024 KBytes) */
        long fileSizeInMB = fileSizeInKB / 1024;

        if (fileSizeInMB < (isImg ? Constant.MAX_SELECT_IMG : Constant.MAX_SELECT_FILE)) {
            return 0;
        }
        return fileSizeInMB;
    }

}