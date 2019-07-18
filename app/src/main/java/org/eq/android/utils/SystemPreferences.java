package org.eq.android.utils;

import android.content.SharedPreferences.Editor;
import android.util.Base64;


import org.eq.android.common.Constant;
import org.eq.android.common.EQApplication;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Set;


/**
 * 保持键值对XML
 *
 */
public class SystemPreferences {

    /**
     * 保存数据到 系统配置文件
     *
     * @param key
     * @param value
     */
    public static void save(String key, final Object value) {
        if (EQApplication.getInstance().getPreferences() != null) {
            final Editor editor = EQApplication.getInstance().getPreferences().edit();

            if (value instanceof String)
                editor.putString(key, (String) value);
            else if (value instanceof Boolean)
                editor.putBoolean(key, (Boolean) value);
            else if (value instanceof Long)
                editor.putLong(key, (Long) value);
            else if (value instanceof Integer)
                editor.putInt(key, (Integer) value);
            else if (value instanceof Float)
                editor.putFloat(key, (Float) value);
            else if (value instanceof Set)
                editor.putStringSet(key, (Set<String>) value);
            else if (value instanceof List) {
                long userId = SystemPreferences.getLong(Constant.USER_ID);
                final String userKey = key + "_" + userId;
                if (value != null && !((List) value).isEmpty()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 创建字节输出流
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            try {
                                // 创建对象输出流，并封装字节流
                                ObjectOutputStream oos = new ObjectOutputStream(baos);
                                // 将对象写入字节流
                                oos.writeObject(value);
                                // 将字节流编码成base64的字符串
                                String tempBase64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                                editor.putString(userKey, tempBase64);
                                editor.commit();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    return;
                } else {
                    editor.putString(userKey, "");
                }
            }
            editor.commit();
        }

    }

    /**
     * 删除
     *
     * @param key
     */
    public static void remove(String key) {
        if (EQApplication.getInstance().getPreferences() != null) {
            Editor editor = EQApplication.getInstance().getPreferences().edit();
            editor.remove(key).commit();
            editor.commit();
        }
    }

    /**
     * 读取配置信息
     *
     * @param key
     * @return
     */
    public static String getString(String key) {
        return getString(key, null);
    }

    /**
     * 读取配置信息
     *
     * @param key
     * @param defValue 默认值
     * @return
     */
    public static String getString(String key, String defValue) {
        String value = "";
        if (EQApplication.getInstance().getPreferences() != null) {
            value = EQApplication.getInstance().getPreferences()
                    .getString(key, defValue);
        }
        return value;
    }

    public static Boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public static Boolean getBoolean(String key, boolean defValue) {
        boolean value = false;
        if (EQApplication.getInstance().getPreferences() != null) {
            value = EQApplication.getInstance().getPreferences()
                    .getBoolean(key, defValue);
        }
        return value;
    }

    public static int getInt(String key) {
        return getInt(key, 0);
    }

    public static float getFloat(String key) {
        float value = 0f;
        if (EQApplication.getInstance().getPreferences() != null) {
            value = EQApplication.getInstance().getPreferences()
                    .getFloat(key, 0);
        }
        return value;
    }

    public static int getInt(String key, int defValue) {
        int value = 0;
        if (EQApplication.getInstance().getPreferences() != null) {
            value = EQApplication.getInstance().getPreferences()
                    .getInt(key, defValue);
        }
        return value;
    }

    public static long getLong(String key) {
        return getLong(key, 0);
    }

    public static long getLong(String key, long defValue) {
        long value = 0;
        if (EQApplication.getInstance().getPreferences() != null) {
            value = EQApplication.getInstance().getPreferences()
                    .getLong(key, defValue);
        }
        return value;
    }

    //	public static Set<String> getSetString(String key) {
    //		return getSetString(key);
    //	}

    public static Set<String> getSetString(String key, Set<String> setString) {
        Set<String> value = null;
        if (EQApplication.getInstance().getPreferences() != null) {
            value = EQApplication.getInstance().getPreferences()
                    .getStringSet(key, setString);
        }
        return value;
    }


}
