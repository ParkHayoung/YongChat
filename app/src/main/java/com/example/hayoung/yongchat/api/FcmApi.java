package com.example.hayoung.yongchat.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by brannpark on 2017. 11. 14..
 */


public interface FcmApi {

    @POST("/fcm/send")
    @Headers({"Content-Type:application/json"})
    Call<ResponseBody> sendMessage(@Header("Authorization") String apiKey, @Body FcmSendMessageBody body);
}
