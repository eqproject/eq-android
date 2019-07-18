package org.eq.android.entity;

/**
 * 进行中订单 API
 * 改版
 */

public class OrderTradeProcessingNew extends OrderTrade {

    private String productImg;//图片url
    private String productName;//商品名称
    private int unitPrice;//面值(单位:分)
    private String userNickName;//用户昵称
    private String photoHead;//用户头像
    private String updateTime;//最后一次交易时间
    private String stateRemark;//状态描述（原型右上角）
    private int allAppeal;//1为可申诉其他不允许申诉
    private int saleNumber; //未卖
    private int saleedNumber; //已买
    private int orderType; //订单类型 订单类型(1:出售广告2:求购广告)
    private int userBoundState; //已经认证 2
    private int tradeNum; //交易订单数
    private float tradeRate; //交易完成率
    private String orderCode;
    private int orderNumber;//订单数量
    private int price; //售卖价格
    private String nickName;


    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
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

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getPhotoHead() {
        return photoHead;
    }

    public void setPhotoHead(String photoHead) {
        this.photoHead = photoHead;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }


    public String getStateRemark() {
        return stateRemark;
    }

    public void setStateRemark(String stateRemark) {
        this.stateRemark = stateRemark;
    }

    public int getAllAppeal() {
        return allAppeal;
    }

    public void setAllAppeal(int allAppeal) {
        this.allAppeal = allAppeal;
    }

    public int getSaleNumber() {
        return saleNumber;
    }

    public void setSaleNumber(int saleNumber) {
        this.saleNumber = saleNumber;
    }

    public int getSaleedNumber() {
        return saleedNumber;
    }

    public void setSaleedNumber(int saleedNumber) {
        this.saleedNumber = saleedNumber;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public int getUserBoundState() {
        return userBoundState;
    }

    public void setUserBoundState(int userBoundState) {
        this.userBoundState = userBoundState;
    }

    public int getTradeNum() {
        return tradeNum;
    }

    public void setTradeNum(int tradeNum) {
        this.tradeNum = tradeNum;
    }

    public float getTradeRate() {
        return tradeRate;
    }

    public void setTradeRate(float tradeRate) {
        this.tradeRate = tradeRate;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
