package org.eq.android.entity;

/**
 * Created by liufan on 2019/6/15.
 */

public class OrderTrade {

    private String tradeNo;//交易订单号
    private String payNo;//支付流水号
    private int amount;//商品售卖价格(单位:分)
    private int orderNum;//订单数量
    private int salePrice;//商品售价(单位:分)
    private int serviceFee;//服务费(单位:分)
    private int status;//状态:(4:支付成功;5:支付失败;6:区块链处理中;7:放款中;8:放款失败;)
    private String createTime;//交易时间
    private String payTime;//支付完成时间
    private int remindPay;//是否已催 (0:未催 1：已催)  <br />**只有原始状态为 3 或者5且为未催。方可进行电催** |
    private int payTimeOut;//付款超时

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getPayNo() {
        return payNo;
    }

    public void setPayNo(String payNo) {
        this.payNo = payNo;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public int getRemindPay() {
        return remindPay;
    }

    public void setRemindPay(int remindPay) {
        this.remindPay = remindPay;
    }

    public int getPayTimeOut() {
        return payTimeOut;
    }

    public void setPayTimeOut(int payTimeOut) {
        this.payTimeOut = payTimeOut;
    }
}
