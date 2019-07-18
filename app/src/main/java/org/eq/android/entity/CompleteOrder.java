package org.eq.android.entity;

/**
 * Created by liufan on 2019/6/30.
 */

public class CompleteOrder {

    private String productImg;
    private int productId;
    private String productName;
    private int unitPrice;
    private String tradeNo;
    private String orderNo;
    private int amount;
    private int orderNum;
    private int salePrice;
    private int serviceFee;
    private String status;
    private String finishTime;
    private int type;
    private String userHeadImg;
    private int orderAdNum;
    private int orderTradeNum;//广告数量
    private int orderAdTradeNum;//广告中已交易数量
    private String orderWantNumberRemark;//发布出售
    private String orderFinishNumberRemark;//已售

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public int getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(int salePrice) {
        this.salePrice = salePrice;
    }

    public int getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(int serviceFee) {
        this.serviceFee = serviceFee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserHeadImg() {
        return userHeadImg;
    }

    public void setUserHeadImg(String userHeadImg) {
        this.userHeadImg = userHeadImg;
    }

    public int getOrderAdNum() {
        return orderAdNum;
    }

    public void setOrderAdNum(int orderAdNum) {
        this.orderAdNum = orderAdNum;
    }

    public int getOrderTradeNum() {
        return orderTradeNum;
    }

    public void setOrderTradeNum(int orderTradeNum) {
        this.orderTradeNum = orderTradeNum;
    }

    public int getOrderAdTradeNum() {
        return orderAdTradeNum;
    }

    public void setOrderAdTradeNum(int orderAdTradeNum) {
        this.orderAdTradeNum = orderAdTradeNum;
    }

    public String getOrderWantNumberRemark() {
        return orderWantNumberRemark;
    }

    public void setOrderWantNumberRemark(String orderWantNumberRemark) {
        this.orderWantNumberRemark = orderWantNumberRemark;
    }

    public String getOrderFinishNumberRemark() {
        return orderFinishNumberRemark;
    }

    public void setOrderFinishNumberRemark(String orderFinishNumberRemark) {
        this.orderFinishNumberRemark = orderFinishNumberRemark;
    }
}
