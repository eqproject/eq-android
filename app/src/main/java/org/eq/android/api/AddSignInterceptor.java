package org.eq.android.api;

import android.util.Base64;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 给网络请求增加签名
 */
public class AddSignInterceptor implements Interceptor {

    private String key;

    public AddSignInterceptor(String key) {
        this.key = key;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String method = request.method();
        if (method.equals("GET")) {
            HttpUrl httpUrl = request.url();
            List<String> signParams = new LinkedList<>();
            Set<String> nameList = httpUrl.queryParameterNames();
            for (String name : nameList) {
                List<String> valueList = httpUrl.queryParameterValues(name);
                if (null != valueList && valueList.size() > 0) {
                    signParams.add(name + "=" + valueList.get(0));
                } else {
                    signParams.add(name + "=");
                }
            }
            String sign = getSign(signParams);
            HttpUrl newHttpUrl = httpUrl.newBuilder()
                    .setEncodedQueryParameter("sign", sign)
                    .build();
            Request.Builder requestBuilder = request.newBuilder().url(newHttpUrl);
            return chain.proceed(requestBuilder.build());
        } else if (method.equals("POST")) {
            RequestBody requestBody = request.body();

            if(requestBody instanceof okhttp3.MultipartBody){
                return chain.proceed(chain.request());
            }

            FormBody formBody = (FormBody) requestBody;
            FormBody.Builder newFormBodyBuilder = new FormBody.Builder();
            List<String> signParams = new LinkedList<>();
            int fieldSize = formBody.size();

            for (int i = 0; i < fieldSize; i++) {
                String name = formBody.name(i);
                String value = formBody.value(i);
                signParams.add(name + "=" + value);
                newFormBodyBuilder.add(name, value);
            }

            String sign = getSign(signParams);
            newFormBodyBuilder.add("sign", sign);

            request = request.newBuilder()
                    .method(request.method(),
                            newFormBodyBuilder.build())
                    .build();
            return chain.proceed(request);
        }

        return chain.proceed(chain.request());
    }


    /**
     * 拿到签名字符
     */
    private String getSign(List<String> signParams) {
        String sign = "";
        Collections.sort(signParams);
        StringBuffer sb = new StringBuffer();
        for (String str : signParams) {
            sb.append(str).append("&");
        }

//        sb.append(String.valueOf(System.currentTimeMillis())).append("&");
        sb.append(key);

        try {

            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(sb.toString().getBytes("UTF-8"));
            byte[] digest = md.digest();

            sign = String.format("%064x", new BigInteger(1, digest));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sign;
    }


}
