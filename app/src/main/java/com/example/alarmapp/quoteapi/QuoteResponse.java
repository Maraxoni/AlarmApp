package com.example.alarmapp.quoteapi;
import com.google.gson.annotations.SerializedName;

public class QuoteResponse {
    @SerializedName("content")
    private String content;

    @SerializedName("author")
    private String author;

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }
}