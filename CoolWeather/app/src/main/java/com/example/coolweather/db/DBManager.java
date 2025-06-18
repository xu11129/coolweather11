package com.example.coolweather.db;

import static android.content.ContentValues.TAG;

import android.util.Log;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class DBManager {
    public  List<Favorite> favoriteList = LitePal.findAll(Favorite.class);

    public void addCityInfo(String cityName, String weatherId){
        List<Favorite> canAdd = LitePal.where("countyName = ?",cityName).find(Favorite.class);
        if(canAdd.size() != 0){
            Log.d(TAG, "addCityInfo: city has existed");
        }else {
            Favorite favorite =new Favorite();
            favorite.setId(favoriteList.size());
            favorite.setCountyName(cityName);
            favorite.setWeatherId(weatherId);
            favorite.save();
        }
    }

    public void deleteCityInfo(String cityName){
        List<Favorite> canDelete = LitePal.where("countyName = ?",cityName).find(Favorite.class);
        if(canDelete.size() != 0 ){
            LitePal.deleteAll(Favorite.class,"countyName = ?",cityName);
        }else{
            Log.d(TAG, "deleteCityInfo: there is no county.");
        }
    }

    public List<String> findAllCityName(){
        List<String> new_cityName = new ArrayList<>();
        for(int i=0;i<favoriteList.size();i++){
            new_cityName.add(favoriteList.get(i).getCountyName());
        }
        if (new_cityName==null)
            Log.d(TAG, "findAllCityName: NULL!");
        else
            Log.d(TAG, "findAllCityName: "+new_cityName);
        return new_cityName;
    }

    public List<String> findAllWeatherId(){
        List<String> new_weatherId = new ArrayList<>();
        for(int i=0;i<favoriteList.size();i++){
            new_weatherId.add(favoriteList.get(i).getWeatherId());
        }
        if (new_weatherId==null)
            Log.d(TAG, "findAllCityName: NULL!");
        else
            Log.d(TAG, "findAllCityName: "+new_weatherId);
        return new_weatherId;
    }

    public boolean checkCity(String cityName){
        List<Favorite> county = LitePal.where("countyName = ?",cityName).find(Favorite.class);
        if (county.size()!=0){
            return true;
        }else {
            return false;
        }
    }

    public int checkId(String cityName){
        List<Favorite> county = LitePal.where("countyName = ?",cityName).find(Favorite.class);
        if (county.size()!=0){
            return county.get(0).getId();
        }else {
            return -1;
        }
    }

    public void clearAllData(){
        if (favoriteList.size() != 0){
            LitePal.deleteAll(Favorite.class);
        }else {
            Log.d(TAG, "clearAllData: no data to clear");
        }
    }

    public String getWeatherId(String cityName){
        String weatherId = null;
        List<Favorite> county = LitePal.where("countyName = ?",cityName).find(Favorite.class);
        if(county.size() != 0 ){
            weatherId = county.get(0).getWeatherId();
        }else{
            Log.d(TAG, "searchCityInfo: there is no county.");
        }
        return weatherId;
    }
}
