package org.eq.android.api;

import org.eq.android.entity.ADOrder;
import org.eq.android.entity.OrderTrade;
import org.eq.android.entity.OrderTradeProcessing;
import org.eq.android.entity.PoolInfo;
import org.eq.android.entity.Product;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 用户接口
 */
public interface OrderApi {

    /**
     * 4-01. 创建交易订单接口
     */
    @FormUrlEncoded
    @POST("api/order/trade/create")
    Call<ResponseData<OrderTrade>> createOrder(@Field("userId") int userId,
                                               @Field("adNo") String adNo,
                                               @Field("orderNum") int orderNum,
                                               @Field("salePrice") int salePrice,
                                               @Field("remarks") String remarks);

    /**
     * 4-02. 取消交易订单接口
     */
    @FormUrlEncoded
    @POST("api/order/trade/cancel")
    Call<ResponseData<OrderTrade>> cancelTradeOrder(@Field("tradeNo") String tradeNo,
                                                    @Field("userId") long userId);

    /**
     * 4-03. 查询交易订单详情接口
     */
    @FormUrlEncoded
    @POST("api/order/trade/detail")
    Call<ResponseData<OrderTradeProcessing>> getDetails(@Field("tradeNo") String tradeNo);


    /**
     * 4-04. 支付结果回调接口
     */
    @FormUrlEncoded
    @POST("api/order/trade/pay/notify")
    Call<ResponseData> payNotify(@Field("tradeNo") String tradeNo,
                                 @Field("payNo") String payNo,
                                 @Field("payAmout") int payAmout,
                                 @Field("payType") int payType,
                                 @Field("payStatus") int payStatus);

    /**
     * 4-08. 支付前校验接口(L)
     */
    @FormUrlEncoded
    @POST("api/order/trade/prePay")
    Call<ResponseData> prePay(@Field("tradeNo") String tradeNo,
                              @Field("userId") int userId);

    /**
     * 4-09.催支付接口
     */
    @FormUrlEncoded
    @POST("api/order/trade/remind")
    Call<ResponseData> remindPay(@Field("tradeNo") String tradeNo);

    /**
     * 4-10.申诉接口
     */
    @FormUrlEncoded
    @POST("api/order/trade/appeal")
    Call<ResponseData> appeal(@Field("tradeNo") String tradeNo,
                              @Field("userId") int userId);

    /**
     * 5-08 获取交易数量（L）
     */
    @FormUrlEncoded
    @POST("api/order/trade/poolInfo")
    Call<ResponseData<PoolInfo>> poolInfo(@Field("userId") long userId);
}
