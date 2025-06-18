package com.example.coolweather;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.coolweather.citymanager.CityManagerActivity;
import com.example.coolweather.db.DBManager;
import com.example.coolweather.gson.Forecast;
import com.example.coolweather.gson.Weather;

public class CityWeatherFragment extends Fragment{
    ScrollView weatherLayout;
    TextView titleCity;
    TextView degreeText;
    TextView weatherInfoText;
    LinearLayout forecastLayout;
    TextView aqiText;
    TextView pm25Text;
    TextView comfortText;
    TextView carWashText;
    TextView sportText;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.fragment_cityweather,container,false);
        initView(view);
            Bundle bundle = getArguments();
            if (bundle!=null){
                String cityName = bundle.getString("countyName");
                //Log.d("test8", "onCreateView: "+cityName);
                DBManager dbManager = new DBManager();
                String weatherId = dbManager.getWeatherId(cityName);
                //Log.d("test9", "onCreateView: "+weatherId);
                WeatherActivity activity = (WeatherActivity) getActivity();
                activity.requestWeather(weatherId);
            }

        return view;
    }
    private void initView(View view){
        weatherLayout=view.findViewById(R.id.weather_layout);
        titleCity=view.findViewById(R.id.title_city);
        degreeText=view.findViewById(R.id.degree_text);
        weatherInfoText=view.findViewById(R.id.weather_info_text);
        forecastLayout=view.findViewById(R.id.forecast_layout);
        aqiText=view.findViewById(R.id.aqi_text);
        pm25Text=view.findViewById(R.id.pm25_text);
        comfortText=view.findViewById(R.id.comfort_text);
        carWashText=view.findViewById(R.id.car_wash_text);
        sportText=view.findViewById(R.id.sport_text);
    }
    public void showWeatherInfo(Weather weather)
    {
        weatherLayout.smoothScrollTo(0,0);
        String cityName = weather.basic.cityName;
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        //Log.d("test22", "showWeatherInfo: "+cityName+" "+degree+" "+weatherInfo);
        titleCity.setText(cityName);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = (TextView)view.findViewById(R.id.date_text);
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            TextView degreeText = (TextView)view.findViewById(R.id.max_min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            degreeText.setText(forecast.temperature.max+"℃ / "+forecast.temperature.min+"℃");
            forecastLayout.addView(view);
        }
        if(weather.aqi!= null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运动建议：" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }
}
