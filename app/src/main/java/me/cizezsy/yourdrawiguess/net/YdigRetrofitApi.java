package me.cizezsy.yourdrawiguess.net;


import me.cizezsy.yourdrawiguess.model.Message;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface YdigRetrofitApi {
    @FormUrlEncoded
    @POST("signup")
    Observable<Message> getVerificationCode(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("signup")
    Observable<Message> signUp(@Field("username") String username,
                               @Field("password") String password,
                               @Field("phone") String phone,
                               @Field("verificationCode") String verificationCode);

    @FormUrlEncoded
    @POST("login")
    Observable<Message> login(@Field("phone") String phone, @Field("password") String password);

    @GET("room/${page}/${num}")
    Observable<Message> getRoomList(@Path("page") int page, @Path("num") int num);
}
