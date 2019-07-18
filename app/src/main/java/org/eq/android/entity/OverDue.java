package org.eq.android.entity;

import java.io.Serializable;

/**
 * 失效劵
 */

public class OverDue implements Serializable{

    private long userId;//用户id
    private String productId;//商品ID
    private String productName;//商品名称
    private String img;//商品图片
    private String number;//商品数量
    private int unitPrice;//商品面值
    private String overdueReason;//失效原因
    private String sort;//用户id
    private int effectNumber;//有效量
    private int lockNumber;//锁定量
    private String transCode;//转出订单号
    private String transNumber;//交易数量


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getOverdueReason() {
        return overdueReason;
    }

    public void setOverdueReason(String overdueReason) {
        this.overdueReason = overdueReason;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public int getEffectNumber() {
        return effectNumber;
    }

    public void setEffectNumber(int effectNumber) {
        this.effectNumber = effectNumber;
    }

    public int getLockNumber() {
        return lockNumber;
    }

    public void setLockNumber(int lockNumber) {
        this.lockNumber = lockNumber;
    }

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    public String getTransNumber() {
        return transNumber;
    }

    public void setTransNumber(String transNumber) {
        this.transNumber = transNumber;
    }
}
