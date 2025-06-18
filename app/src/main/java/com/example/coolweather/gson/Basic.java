package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {
    /*
    @SerializedName("humidity")
    public String humidity;
    @SerializedName("aqi")
    public String aqi;
    public Wind wind;
    public class Wind{
        @SerializedName("direct")
        public String direct;
        @SerializedName("power")
        public String power;
    }
*/
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;

}
