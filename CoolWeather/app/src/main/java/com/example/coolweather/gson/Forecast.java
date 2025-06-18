package com.example.coolweather.gson;


import com.google.gson.annotations.SerializedName;

public class Forecast {

    public String date;
   // @SerializedName("tmpperature")
    @SerializedName("tmp")
    public Temperature temperature;
   // @SerializedName("weather")
  //  public String weather;
    //@SerializedName("direct")
  //  public String direct;
    @SerializedName("cond")
    public More more;
    public class Temperature{
        public String max;
        public String min;
    }
    public class More{
        @SerializedName("txt_d")
        public String info;
    }
}
