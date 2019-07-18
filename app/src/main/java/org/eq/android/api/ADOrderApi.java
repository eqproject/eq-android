package org.eq.android.api;

import org.eq.android.entity.ADOrder;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 用户接口
 */
public interface ADOrderApi {


    /**
     * 3-01 创建广告订单接口（L）
     */
    @FormUrlEncoded
    @POST("api/adOrder/user/create")
    Call<ResponseData<ADOrder>> createADOrder(@Field("productId") int productId,
                                              @Field("adTitle") String adTitle,
                                              @Field("price") int price,
                                              @Field("number") int number,
                                              @Field("userId") int userId,
                                              @Field("orderType") int orderType);


    /**
     * 3-02 关闭订单（L）
     */
    @FormUrlEncoded
    @POST("api/adOrder/user/cancel")
    Call<ResponseData> cancelADOrder(@Field("orderCode") String productId,
                                              @Field("userId") int adTitle);


    /**
     * 3-03 获取集市订单数据（L）
     */
    @FormUrlEncoded
    @POST("api/adOrder/plat/list")
    Call<ResponseData<EntityList<ADOrder>>> getADOrderList(@Field("userId") int userId,
                                                           @Field("pageSize") int pageSize,
                                                           @Field("pageNum") int pageNum,
                                                           @Field("orderType") String orderType);

    /**
     * 3-03 获取集市订单数据（L）2
     * 不传用户 Id
     */
    @FormUrlEncoded
    @POST("api/adOrder/plat/list")
    Call<ResponseData<EntityList<ADOrder>>> getADOrderList(@Field("pageSize") int pageSize,
                                                           @Field("pageNum") int pageNum,
                                                           @Field("orderType") String orderType);

    /**
     * 3-05 获取订单详情（L）
     */
    @FormUrlEncoded
    @POST("api/adOrder/plat/details")
    Call<ResponseData<ADOrder>> getADOrder(@Field("orderCode") String orderCode,
                                           @Field("userId") int userId);
}
