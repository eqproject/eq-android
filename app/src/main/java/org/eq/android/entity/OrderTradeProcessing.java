package org.eq.android.entity;

/**
 * Created by liufan on 2019/6/15.
 */

public class OrderTradeProcessing {

    private Product product;
    private OrderTrade trade;
    private User user;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public OrderTrade getTrade() {
        return trade;
    }

    public void setTrade(OrderTrade trade) {
        this.trade = trade;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
