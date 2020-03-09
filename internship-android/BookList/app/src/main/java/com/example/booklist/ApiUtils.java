package com.example.booklist;

public class ApiUtils {
    public ApiUtils(){
    }

    public static final String BASE_URL = "http://tiendemo.gearhostpreview.com/";

    public static APIInterface getAPIService(){
        return RetrofitBook.getBook(BASE_URL).create(APIInterface.class);
    }
}
