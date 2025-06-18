package com.example.coolweather.db;

import org.litepal.crud.LitePalSupport;

public class Favorite extends LitePalSupport {
    private int id;
    private String countyName;
    private String weatherId;

    public Favorite(){

    }
    public Favorite(int id,String countyName,String weatherId) {
        this.id = id;
        this.countyName = countyName;
        this.weatherId = weatherId;
    }

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id=id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}
