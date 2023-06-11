package com.example.h071211028_finallmobile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CastResponse {
    @SerializedName("cast")
    @Expose
    private List<Casts> casts;

    public List<Casts> getCasts(){
        return casts;
    }
}
