package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    public String status;
    //@SerializedName("city")
    public String city;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;
    //@SerializedName("future")
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
