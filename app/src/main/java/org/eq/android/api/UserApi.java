package org.eq.android.api;

import org.eq.android.entity.ADOrder;
import org.eq.android.entity.OverDue;
import org.eq.android.entity.User;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * 用户接口
 */
public interface UserApi {

    /**
     * 1-03.用户信息维护接口
     * @param id
     * @param name
     * @param nickname
     * @param birthday
     * @param photoHead
     * @param intro
     * @return
     */
    @FormUrlEncoded
    @POST("api/user/modify")
    Call<ResponseData> modifyUser(@Field("id") long id ,
                                  @Field("name") String name,
                                  @Field("nickname") String nickname,
                                  @Field("birthday") String birthday,
                                  @Field("photoHead") String photoHead,
                                  @Field("intro") String intro,
                                  @Field("sex") int sex);

    /**
     * 1-05.用户信息维护接口
     * @param userId
     * @param identityName
     * @param identityCard
     * @return
     */
    @FormUrlEncoded
    @POST("api/user/identity/verify")
    Call<ResponseData> verify(@Field("userId") long userId ,
                                  @Field("identityName") String identityName,
                                  @Field("identityCard") String identityCard);

    @GET("api/user/register")
    Call<ResponseData<OverDue>> registerUser(@Query("mobile") String mobile,
                                             @Query("captcha") String captcha);

    @FormUrlEncoded
    @POST("api/user/reset")
    Call<ResponseData<User>> resetPassword(@Field("mobile") String mobile,
                                         @Field("captcha") String captcha,
                                         @Field("pwd") String pwd);

    @FormUrlEncoded
    @POST("api/user/reset")
    Call<ResponseData<User>> resetPassword(@Field("userId") long userId ,
                                           @Field("pwd") String pwd);


    @GET("api/sms/send")//短信类型：1,登陆注册验证码2,发布广告成功通知3, 数字券已转到券包通知"4, 数字券已成功转出通知5,订单确认通知
    Call<ResponseData> sendSms(@Query("mobile") String mobile,
                               @Query("type") int type);

    @FormUrlEncoded
    @POST("api/user/login")
    Call<ResponseData<User>> login(@Field("mobile") String mobile,
                                   @Field("pwd") String pwd);

    @GET("api/user/getInfo")
    Call<ResponseData<User>> getInfo(@Query("userId") long userId);

    @Multipart
    @POST("api/user/upload/head")
    Call<ResponseData<User>> uploadFile(@Part MultipartBody.Part file);

//    新建 GET 和 POST 接口格式同上
//    用法：
//        异步：
//            NetFactory.create(UserApi.class).registerUser(mobile, captcha).enqueue(new Callback<ResponseData>() {
//                @Override
//                public void onResponse (Call < ResponseData > call, Response < ResponseData > response){
//                    ResponseData responseData = response.body();
//                }
//
//                @Override
//                public void onFailure (Call < ResponseData > call, Throwable t){
//                    请求失败
//                }
//            });
//        同步：
//            Response<ResponseData> execute = NetFactory.create(UserApi.class).registerUser(mobile, captcha).execute();

}
