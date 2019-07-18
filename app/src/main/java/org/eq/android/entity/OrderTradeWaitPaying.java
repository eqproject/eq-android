package org.eq.android.entity;

/**
 * 待付款列表
 */
public class OrderTradeWaitPaying {

    private OrderTradeUser orderTradeUser;
    private Trade trade;
    private Long expireTime;

    public OrderTradeUser getOrderTradeUser() {
        return orderTradeUser;
    }

    public void setOrderTradeUser(OrderTradeUser orderTradeUser) {
        this.orderTradeUser = orderTradeUser;
    }

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public static class OrderTradeUser {
        private int sellUserId;
        private String sellUserNickName;
        private int buyUserId;
        private String buyUserNickName;
        private String phoneHead;
        private int authstatus;

        public int getSellUserId() {
            return sellUserId;
        }

        public void setSellUserId(int sellUserId) {
            this.sellUserId = sellUserId;
        }

        public String getSellUserNickName() {
            return sellUserNickName;
        }

        public void setSellUserNickName(String sellUserNickName) {
            this.sellUserNickName = sellUserNickName;
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

        public String getPhoneHead() {
            return phoneHead;
        }

        public void setPhoneHead(String phoneHead) {
            this.phoneHead = phoneHead;
        }

        public int getAuthstatus() {
            return authstatus;
        }

        public void setAuthstatus(int authstatus) {
            this.authstatus = authstatus;
        }
    }

    public static class Trade {
        private String tradeNo;
        private int unitPrice;
        private int amount;
        private int orderNum;
        private int salePrice;
        private int serviceFee;
        private int remindPay;
        private int status;
        private String createTime;
        private int payTimeOut;
        private String updateTime;
        private String productImg;
        private String productName;
        private String payNo;
        private String payTime;
        private String stateRemark;

        public String getTradeNo() {
            return tradeNo;
        }

        public void setTradeNo(String tradeNo) {
            this.tradeNo = tradeNo;
        }

        public int getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(int unitPrice) {
            this.unitPrice = unitPrice;
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

        public int getRemindPay() {
            return remindPay;
        }

        public void setRemindPay(int remindPay) {
            this.remindPay = remindPay;
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

        public int getPayTimeOut() {
            return payTimeOut;
        }

        public void setPayTimeOut(int payTimeOut) {
            this.payTimeOut = payTimeOut;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

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

        public String getPayNo() {
            return payNo;
        }

        public void setPayNo(String payNo) {
            this.payNo = payNo;
        }

        public String getPayTime() {
            return payTime;
        }

        public void setPayTime(String payTime) {
            this.payTime = payTime;
        }

        public String getStateRemark() {
            return stateRemark;
        }

        public void setStateRemark(String stateRemark) {
            this.stateRemark = stateRemark;
        }
    }

}
