package org.eq.android.api;

import org.eq.android.entity.AcceptDetail;
import org.eq.android.entity.CompleteOrder;
import org.eq.android.entity.OrderTradeProcessing;
import org.eq.android.entity.OrderTradeProcessingNew;
import org.eq.android.entity.OrderTradeWaitPaying;
import org.eq.android.entity.OverDue;
import org.eq.android.entity.ResponseNode;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 劵包
 */

public interface VoucherApi {

    /**
     * 3-04   获取用户个人发布中的广告订单
     */
    @FormUrlEncoded
    @POST("api/adOrder/user/list")
    Call<ResponseData<EntityList<OrderTradeProcessingNew>>> getUserAdorderList(@Field("userId") int userId,
                                                                               @Field("pageSize") int pageSize,
                                                                               @Field("pageNum") int pageNum,
                                                                               @Field("orderType") int orderType);

    /**
     * 4-05 查询待付款交易订单列表接口
     */
    @FormUrlEncoded
    @POST("api/order/trade/paying/list")
    Call<ResponseData<EntityList<OrderTradeWaitPaying>>> getPayingList(@Field("userId") int userId,
                                                                       @Field("pageSize") int pageSize,
                                                                       @Field("pageNum") int pageNum);

    /**
     * 4-06 进行中
     */
    @FormUrlEncoded
    @POST("api/order/trade/porcessing/list")
    Call<ResponseData<EntityList<OrderTradeProcessingNew>>> getProcessingList(@Field("userId") int userId,
                                                                              @Field("pageSize") int pageSize,
                                                                              @Field("pageNum") int pageNum);

    /**
     * 4-07 查询已完成（广告+交易）订单列表接口
     */
    @FormUrlEncoded
    @POST("api/order/finish/list")
    Call<ResponseData<EntityList<CompleteOrder>>> getFinishList(@Field("userId") int userId,
                                                                @Field("pageSize") int pageSize,
                                                                @Field("pageNum") int pageNum);

    /**
     * 5-01持有劵
     */
    @FormUrlEncoded
    @POST("api/voucher/user/effectList")
    Call<ResponseData<EntityList<OverDue>>> getEffectList(@Field("userId") int userId,
                                                          @Field("pageSize") int pageSize,
                                                          @Field("pageNum") int pageNum);

    /**
     * 5-02 转出用户券
     */
    @FormUrlEncoded
    @POST("api/voucher/user/turnout")
    Call<ResponseData<EntityList<OverDue>>> trunout(@Field("userId") int userId,
                                                    @Field("productId") String productId,
                                                    @Field("number") int number,
                                                    @Field("address") String address);

    /**
     * 5-03  兑换用户商品
     */
    @FormUrlEncoded
    @POST("api/voucher/user/accept")
    Call<ResponseData<AcceptDetail>> getAccept(@Field("userId") int userId,
                                               @Field("productId") String productId,
                                               @Field("number") int number,
                                               @Field("consignee") String consignee,
                                               @Field("consigneePhone") String consigneePhone,
                                               @Field("consigneeAddress") String consigneeAddress);


    /**
     * 5-04 承兑中
     */
    @FormUrlEncoded
    @POST("api/voucher/user/acceptList")
    Call<ResponseData<EntityList<OverDue>>> getAcceptList(@Field("userId") int userId,
                                                          @Field("pageSize") int pageSize,
                                                          @Field("pageNum") int pageNum);


    /**
     * 5-06无效券
     */
    @FormUrlEncoded
    @POST("api/voucher/user/overdueList")
    Call<ResponseData<EntityList<OverDue>>> getOverDueList(@Field("userId") int userId,
                                                           @Field("pageSize") int pageSize,
                                                           @Field("pageNum") int pageNum);

    /**
     * 5-07 承兑订单详情
     */
    @FormUrlEncoded
    @POST("api/voucher/user/acceptDetail")
    Call<ResponseData<AcceptDetail>> getAcceptDetail(@Field("userId") int userId,
                                                     @Field("acceptCode") String acceptCode);

    /**
     * 8-02 获取法务支持
     */
    @GET("api/support/legal")
    Call<ResponseData<ResponseNode>> getSupportLegal();

    /**
     * 8-04 获取平台配置项
     */
    @FormUrlEncoded
    @POST("api/support/getConfig")
    Call<ResponseData> getConfig();
}
