package org.eq.android.entity;

import java.util.Objects;

/**
 * 订单
 */
public class ADOrder {
    private String orderCode; //订单号
    private int productId; //商品id
    private String productName; //商品名称
    private String img; //商品图片
    private int price; //销售单价
    private int orderNumber; //订单总量
    private int saleedNumber; //已售卖量
    private int saleNumber; //可交易量
    private String title; //订单标题
    private int userId; //用户Id
    private String userImg; //用户头像
    private String nickName; //用户昵称
    private int userBoundState; //1:仅支付宝 2:微信 3:支付宝微信均绑定
    private int orderType;
    private String createTime;
    private int tradeNum;
    private double tradeRate;
    private int unitPrice;
    private String acceptName;

    public ADOrder() {
    }

    public ADOrder(String orderCode, int productId, String productName, String img, int price, int orderNumber, int saleedNumber, int saleNumber, String title, int userId, String userImg, String nickName, int userBoundState, int orderType, String createTime, int tradeNum, int tradeRate) {
        this.orderCode = orderCode;
        this.productId = productId;
        this.productName = productName;
        this.img = img;
        this.price = price;
        this.orderNumber = orderNumber;
        this.saleedNumber = saleedNumber;
        this.saleNumber = saleNumber;
        this.title = title;
        this.userId = userId;
        this.userImg = userImg;
        this.nickName = nickName;
        this.userBoundState = userBoundState;
        this.orderType = orderType;
        this.createTime = createTime;
        this.tradeNum = tradeNum;
        this.tradeRate = tradeRate;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getSaleedNumber() {
        return saleedNumber;
    }

    public void setSaleedNumber(int saleedNumber) {
        this.saleedNumber = saleedNumber;
    }

    public int getSaleNumber() {
        return saleNumber;
    }

    public void setSaleNumber(int saleNumber) {
        this.saleNumber = saleNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getUserBoundState() {
        return userBoundState;
    }

    public void setUserBoundState(int userBoundState) {
        this.userBoundState = userBoundState;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public int getTradeNum() {
        return tradeNum;
    }

    public void setTradeNum(int tradeNum) {
        this.tradeNum = tradeNum;
    }

    public double getTradeRate() {
        return tradeRate;
    }

    public void setTradeRate(double tradeRate) {
        this.tradeRate = tradeRate;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getAcceptName() {
        return acceptName;
    }

    public void setAcceptName(String acceptName) {
        this.acceptName = acceptName;
    }
}
