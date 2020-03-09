package com.jvit.companycoin.api;


import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiClient {

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @FormUrlEncoded
    @POST("/api/authorizations")
    Call<Login> LOGIN_CALL(
            @Field("email") String email,
            @Field("password") String password);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @GET("/api/user")
    Call<UserLogin> USER_LOGIN_CALL(@Header("Authorization") String token);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @GET("api/users/home-ranking")
    Call<TopRank> TOP_RANK_CALL(@Header("Authorization") String token);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @GET("api/users/ranking")
    Call<AllRank> USER_ALL_RANK_CALL(@Header("Authorization") String token);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @GET("api/users/ranking?")
    Call<AllRank> USER_ALL_RANK_PAGE_CALL(
            @Header("Authorization") String token,
            @Query("page")String page);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @GET("api/gifts")
    Call<AllGiftExchange> ALL_GIFT_EXCHANGE_CALL(@Header("Authorization") String token);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @GET("api/gifts/new")
    Call<NewGiftExchange> NEW_GIFT_EXCHANGE_CALL(@Header("Authorization") String token);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @GET("api/ideas/home")
    Call<IdeaHome> IDEA_HOME_CALL(@Header("Authorization") String token);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @GET("api/ideas/all")
    Call<IdeaAll> IDEA_ALL_CALL(@Header("Authorization") String token);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @GET("api/ideas/all?")
    Call<IdeaAll> IDEA_ALL_PAGE_CALL(
            @Header("Authorization") String token,
            @Query("page") String page);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @GET("api/ideas/new")
    Call<IdeaAll> IDEA_NEW_CALL(@Header("Authorization") String token);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @GET("api/ideas/popular")
    Call<IdeaAll> IDEA_POPULAR_CALL(@Header("Authorization") String token);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @GET("api/page/qna")
    Call<QuestionAndAnswer> QUESTION_AND_ANSWER_CALL(@Header("Authorization") String token);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @FormUrlEncoded
    @POST("api/user/password")
    Call<ChangePassword> CHANGE_PASSWORD_CALL(
            @Header("Authorization") String token,
            @Field("old_password") String old_password,
            @Field("password") String password);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @GET("api/users/find-by-keyword")
    Call<FindUserKeyword> FIND_USER_CALL(@Header("Authorization") String token);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @FormUrlEncoded
    @POST("api/user/sendcoin")
    Call<Coin> COIN_CALL(
            @Header("Authorization") String token,
            @Field("email") String email,
            @Field("token_amount") int token_amount,
            @Field("note") String note);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @FormUrlEncoded
    @POST("api/user/idea")
    Call<SendComment> SEND_COMMENT_CALL(
            @Header("Authorization") String token,
            @Field("content") String content);


    @Headers("Accept: application/vnd.coc-main.v1+json")
    @FormUrlEncoded
    @POST("api/password/email")
    Call<ChangePassword> FORGET_PASSWORD_CALL(
            @Header("Authorization") String token,
            @Field("email") String email,
            @Field("callback_url") String callback_url);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @POST("api/ideas/{id}/like")
    Call<LikeIdea> LIKE_IDEA_CALL(
            @Header("Authorization") String token,
            @Path("id") String id);


    @Headers("Accept: application/vnd.coc-main.v1+json")
    @FormUrlEncoded
    @POST("api/user/exchange")
    Call<BuyGift> BUY_GIFT_CALL(
            @Header("Authorization") String token,
            @Field("gift_id") int id);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @GET("api/ideas/{id}")
    Call<InfoLikeUser> INFO_LIKE_CALL(
            @Header("Authorization") String token,
            @Path("id") int id);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @GET("api/notifications/all")
    Call<AllNofication> ALL_NOTIFICATION_CALL(@Header("Authorization") String token);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @GET("api/notifications/all?")
    Call<AllNofication> ALL_NOTIFICATION_PAGE_CALL(
            @Header("Authorization") String token,
            @Query("page") String page);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @GET("api/notifications/my")
    Call<AllNofication> MY_NOTIFICATION_CALL(@Header("Authorization") String token);


    @Headers("Accept: application/vnd.coc-main.v1+json")
    @GET("api/notifications/my?")
    Call<AllNofication> MY_NOTIFICATION_PAGE_CALL(
            @Header("Authorization") String token,
            @Query("page") String page);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @GET("/api/user/transactions")
    Call<TransactionsHistory> TRANSACTIONS_HISTORY_CALL(
            @Header("Authorization") String token);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @GET("/api/user/transactions")
    Call<TransactionsHistory> TRANSACTIONS_HISTORY_PAGE_CALL(
            @Header("Authorization") String token,
            @Query("page") String page);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @Multipart
    @POST("api/user/avatar")
    Call<UploadAvatar> UPLOAD_AVATAR_CALL(
            @Header("Authorization") String token,
            @Part MultipartBody.Part file);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @GET("api/user/card-info")
    Call<CardInfo> CARD_INFO_CALL(
            @Header("Authorization") String token);

    @Headers("Accept: application/vnd.coc-main.v1+json")
    @FormUrlEncoded
    @POST("api/user/checkin")
    Call<CheckInUser> CHECK_IN_USER_CALL(
            @Header("Authorization") String token);
}
