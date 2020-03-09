package com.example.booklist;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MultipleResource {
    @SerializedName("bookpage_list")
    public List<Book> bookpage_list  = new ArrayList<>();

    public class Book{
        @SerializedName("book_name")
        public String book_name;

        @SerializedName("volume_name")
        public String volume_name;

        @SerializedName("chapter_name")
        public String chapter_name;

        @SerializedName("highlight")
        public String highlight;

        @SerializedName("neighbor_text")
        public String neighbor_text;
    }
}
