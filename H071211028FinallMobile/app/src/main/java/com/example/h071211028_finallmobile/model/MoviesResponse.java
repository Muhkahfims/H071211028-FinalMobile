package com.example.h071211028_finallmobile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MoviesResponse {
    @SerializedName("results")
    @Expose
    private List<Movies> nowPlayings;

    public List<Movies> getNowPlayings(){
        return nowPlayings;
    }

}
