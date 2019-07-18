package org.eq.android.api;

import org.eq.android.entity.Product;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 商品接口
 */
public interface ProductApi {

    /**
     * 2-01 获取平台当前可交易商品信息（L）
     */
    @FormUrlEncoded
    @POST("api/product/platform/effect")
    Call<ResponseData<EntityList<Product>>> getPlatformEffect(@Field("userId") int userId,
                                                              @Field("pageSize") int pageSize,
                                                              @Field("pageNum") int pageNum);

    /**
     * 2-02 获取用户可售卖商品
     */
    @FormUrlEncoded
    @POST("api/product/user/effect")
    Call<ResponseData<EntityList<Product>>> getUserEffect(@Field("userId") int userId,
                                                          @Field("pageSize") int pageSize,
                                                          @Field("pageNum") int pageNum);

    /**
     * 2-03 获取商品详情（L）
     */
    @FormUrlEncoded
    @POST("api/product/platform/details")
    Call<ResponseData<Product>> getPlatformDetails(@Field("userId") int userId,
                                                   @Field("id") int id);

    /**
     * 2-06. 查询自己持有券详情接口（L）
     */
    @FormUrlEncoded
    @POST("api/product/user/details")
    Call<ResponseData<Product>> getMyDetails(@Field("userId") int userId,
                                             @Field("id") int id);

}
