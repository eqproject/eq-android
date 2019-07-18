package org.eq.android.common;

/**
 * 常量类
 */
public interface Constant {
    // 调试模式， 控制日志
    boolean debug = true;

    //base url
//    String baseURL = "http://47.74.190.193:8001";
//    String ReleaseBaseUrl="http://120.27.71.34:8001";
    String baseURL = "http://120.27.71.34:8001";
    String key = "1A9585B3C9F0854AD1B73C4ED80F7D31"; //接口参数签名 key

    //每页加载数量
    int pageSize = 25;

    String passwordKey = "96448f740822fd73517f96a3c3a2487f"; //密码加密 ey

    /**
     * 用户id
     */
    public static final String USER_ID = "userId";
    /**
     * 用户名
     */
    public static final String USER_NAME = "username";
    /**
     * 登录状态
     */
    public static final String ISLOGIN = "isLogin";
    /**
     * 手机号
     */
    public static final String PHONENUMBER = "phoneNumber";
    /**
     * 手机号
     */
    public static final String NICKNAME = "nickName";
    /**
     * 认证
     */
    public static final String AUTHSTATUS = "authStatus";
    /**
     * 是否商户：1非商户;2商户
     */
    public static final String CLIENTTYPE = "clientType";


    /**
     * 图片上传压缩的最大大小(kb)
     */
    public static final double UPLOAD_MAX_SIZE = 1024;

    /**
     * 选择图片限制大小(Mb)
     */
    public static final double MAX_SELECT_IMG = 5;

    /**
     * 选择文件限制大小(Mb)
     */
    public static final double MAX_SELECT_FILE = 20;

    /**
     * 集市收发布广告，出售收到到广播
     */
    public static final String BROADCAST_ACTION_MARK = "org.eq.android.marketbroadcast";

    /**
     * 集市收发布广告，出售收到到广播
     */
    public static final String BROADCAST_ACTION_TICKET = "org.eq.android.ticketbroadcast";
}
