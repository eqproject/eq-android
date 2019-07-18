package org.eq.android.entity;

/**
 * Created by liufan on 2019/6/15.
 */

public class User {

    private int id;//用户id
    private String name;//用户名
    private String nickname;//昵称
    private String birthday;//生日
    private String photoHead;//头像
    private String intro;//简介
    private String sign;//签名
    private String mobile;//手机号
    private int sex;
    private int authStatus;//认证
    private int clientType;//1普通2商户


    private int buyUserId;//买家用户id
    private String buyUserNickName;//买家昵称
    private String sellUserNickName;//售卖用户昵称
    private int sellUserId;//售卖用户id
    private String img;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBuyUserId() {
        return buyUserId;
    }

    public void setBuyUserId(int buyUserId) {
        this.buyUserId = buyUserId;
    }

    public String getBuyUserNickName() {
        return buyUserNickName;
    }

    public void setBuyUserNickName(String buyUserNickName) {
        this.buyUserNickName = buyUserNickName;
    }

    public String getSellUserNickName() {
        return sellUserNickName;
    }

    public void setSellUserNickName(String sellUserNickName) {
        this.sellUserNickName = sellUserNickName;
    }

    public int getSellUserId() {
        return sellUserId;
    }

    public void setSellUserId(int sellUserId) {
        this.sellUserId = sellUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhotoHead() {
        return photoHead;
    }

    public void setPhotoHead(String photoHead) {
        this.photoHead = photoHead;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(int authStatus) {
        this.authStatus = authStatus;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }


    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
