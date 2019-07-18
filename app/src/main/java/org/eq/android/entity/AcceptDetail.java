package org.eq.android.entity;

/**
 * Created by liufan on 2019/6/15.
 */

public class AcceptDetail {

    private int userId;
    private int productId;
    private String acceptCode;//订单号
    private String productName;//商品名称
    private String img;// 商品图片
    private int number;//商品数量
    private String consignee;// 联系人
    private String consigneePhone;//电话号码
    private String consigneeAddress;// 地址
    private int unitPrice;//商品面值
    private String acceptModile;
    private String stateRemak;//状态描述

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getAcceptCode() {
        return acceptCode;
    }

    public void setAcceptCode(String acceptCode) {
        this.acceptCode = acceptCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getConsigneePhone() {
        return consigneePhone;
    }

    public void setConsigneePhone(String consigneePhone) {
        this.consigneePhone = consigneePhone;
    }

    public String getConsigneeAddress() {
        return consigneeAddress;
    }

    public void setConsigneeAddress(String consigneeAddress) {
        this.consigneeAddress = consigneeAddress;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getAcceptModile() {
        return acceptModile;
    }

    public void setAcceptModile(String acceptModile) {
        this.acceptModile = acceptModile;
    }

    public String getStateRemak() {
        return stateRemak;
    }

    public void setStateRemak(String stateRemak) {
        this.stateRemak = stateRemak;
    }
}
