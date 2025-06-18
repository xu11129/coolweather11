package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Now {
 /*   @SerializedName("tmperature")
    public String temperature;
    @SerializedName("info")
    public String info;
*/
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public More more;
    public class More{
        @SerializedName("txt")
        public String info;
    }
}
