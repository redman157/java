package com.jvit.companycoin.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {
    public static String url_path = "https://main.mobile1.companycoin.net/img/";
    public static String url = "https://mobile1.companycoin.net/";
    private static Retrofit retrofit;
    private ApiService(){

    }
    // singleton ap dụng cho nhiều thread
    public static Retrofit getRetrofit(){
        if (retrofit == null) {
            synchronized (Retrofit.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl("https://main.mobile1.companycoin.net")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }

}
