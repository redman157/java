package com.example.booklist;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIInterface {
    @GET("/booklist.txt")
    Call<MultipleResource> doGetListBook();
}
