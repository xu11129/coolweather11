package com.example.coolweather;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.coolweather.citymanager.CityManagerActivity;
import com.example.coolweather.db.DBManager;
import com.example.coolweather.gson.Weather;
import com.example.coolweather.service.AutoUpdateService;
import com.example.coolweather.util.GetAddressUtil;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.LocationUtil;
import com.example.coolweather.util.SettingInfo;
import com.example.coolweather.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends FragmentActivity implements View.OnClickListener{
    public static WeatherActivity wactivity;
    private static String TAG = "WeatherActivity";
    private Button settingButton,locateButton;
    public SwipeRefreshLayout swipeRefresh;
    private ImageView bingPicImg;
    private String mCityName;
    private int currentPos;
    private ViewPager viewPager;
    private ScreenSlidePagerAdapter pagerAdapter;
    private ProgressDialog progressDialog;
    List<CityWeatherFragment> fragmentList;
    List<String> cityList;
    List<String> weatherIdList;
    List<ImageView> imageViewList;
    List<String> tempList;
    List<Weather> weatherList;

    LinearLayout pointLayout;
    private SettingInfo settingInfo = new SettingInfo();
    int[][] bg_images = {{R.mipmap.sunny1,R.mipmap.sunny2,R.mipmap.sunny3},
            {R.mipmap.cloud1,R.mipmap.cloud2,R.mipmap.cloud3},
            {R.mipmap.rainny1,R.mipmap.rainny2,R.mipmap.rainny3}};

    private SharedPreferences prefs ;
    private SharedPreferences prefs2;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //版本号判断
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            View decorView=getWindow().getDecorView();
            int option=View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }

        setContentView(R.layout.activity_weather);
        wactivity=this;
        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);
        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(com.google.android.material.R.color.design_default_color_primary);
        settingButton=(Button)findViewById(R.id.setting_button);
        locateButton=(Button)findViewById(R.id.locate_button);
        pointLayout=(LinearLayout)findViewById(R.id.main_layout_point);
        viewPager=(ViewPager)findViewById(R.id.weather_pager);

        settingButton.setOnClickListener(this);
        locateButton.setOnClickListener(this);

        fragmentList = new ArrayList<>();
        imageViewList = new ArrayList<>();
        tempList = new ArrayList<>();
        weatherList = new ArrayList<Weather>();

        DBManager dbManager = new DBManager();
        cityList = dbManager.findAllCityName();
        weatherIdList=dbManager.findAllWeatherId();
        List<String> permissionList = new ArrayList<>();

        /*申请位置权限*/
        if (ContextCompat.checkSelfPermission(WeatherActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
           permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(WeatherActivity.this,permissions,1);
        }
        prefs = getSharedPreferences("weatherCode", Context.MODE_PRIVATE);
        prefs2= getSharedPreferences("SettingInfo", Context.MODE_PRIVATE);
        //Log.d("test5", "initPager: "+cityList);
        //Log.d("test5", "initPager: "+weatherIdList);
        if (cityList.size() == 0){
            Intent intent = new Intent(this, CityManagerActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this,"当前无城市可查询，请先添加城市",Toast.LENGTH_SHORT).show();

        }else {
            initPager();
            Log.d(TAG, "initPager: 1111");
            pagerAdapter=new ScreenSlidePagerAdapter(getSupportFragmentManager(),fragmentList);
            viewPager.setAdapter(pagerAdapter);
            //initPoint();
            viewPager.setCurrentItem(0);
            currentPos=viewPager.getCurrentItem();
            setPagerListener();
        }
        try {
            Intent intent = getIntent();
            if (intent !=null){
                int cityName_get = intent.getIntExtra("cityName",0);
                //Log.d("test00", "onCreate: "+cityName_get);
                viewPager.setCurrentItem(cityName_get);
                settingInfo = intent.getParcelableExtra("settingInfo");
                closeProgressDialog();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                requestWeather(mCityName);
            }
        });
    }
    protected void onStop(){
        super.onStop();
        currentPos=viewPager.getCurrentItem();
    }
    protected void onResume(){
        super.onResume();
        if (fragmentList.size()!=0){
            requestWeather(weatherIdList.get(currentPos));
            for (int i=0;i<imageViewList.size();i++){
                imageViewList.get(i).setImageResource(R.mipmap.point1);
            }
            imageViewList.get(currentPos).setImageResource(R.mipmap.point2);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.setting_button) {
            showPopupMenu(settingButton);
        }
        else if (id == R.id.locate_button) {
            getMyLocation();
        }
    }
    /*获取当前位置信息*/
    private void getMyLocation(){
        showProgressDialog("正在定位当前位置，请稍等...");
        LocationUtil.getCurrentLocation(this, new LocationUtil.LocationCallBack() {
            @Override
            public void onSuccess(Location location) {
                closeProgressDialog();
                Log.d(TAG, "onSuccess: "+location);
                GetAddressUtil g = new GetAddressUtil(WeatherActivity.this);
                String s = g.getAddress(location.getLongitude(),location.getLatitude());
                //String new_str = s.replace("区_","");
                Toast.makeText(WeatherActivity.this,s,Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(WeatherActivity.this);
                builder.setTitle("目前所在地区为").setMessage(s+"\n是否跳转页面以添加该地区?")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(WeatherActivity.this,CityManagerActivity.class);
                                startActivity(intent);
                            }
                        });
                builder.setNegativeButton("否",null);
                builder.create().show();
                //Log.d(TAG, " "+s);
            }
            @Override
            public void onFail(String msg) {
                closeProgressDialog();
                Log.d(TAG, msg);
            }
        });
    }
//监听左右滑动事件
    private void setPagerListener(){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                mCityName=weatherIdList.get(position);
                currentPos=position;
                //CityWeatherFragment cwFrag = fragmentList.get(viewPager.getCurrentItem());
                //cwFrag.showWeatherInfo(weatherList.get(position));
                requestWeather(weatherIdList.get(position));
                //Log.d("test33", "onPageSelected: "+mCityName);
                //Log.d("test33", "onPageSelected: "+position);
                for (int i=0;i<imageViewList.size();i++){
                    imageViewList.get(i).setImageResource(R.mipmap.point1);
                }
                imageViewList.get(position).setImageResource(R.mipmap.point2);
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }
    private void initPager(){
        DBManager dbManager = new DBManager();
        cityList = dbManager.findAllCityName();
        if (cityList.size()==0){
            Intent intent = new Intent(this,CityManagerActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this,"你把城市删光啦！当前无城市可查询，请先添加城市。",Toast.LENGTH_SHORT).show();
        }
        weatherIdList=dbManager.findAllWeatherId();
            for (int i = 0; i <cityList.size();i++){
                CityWeatherFragment cityWeatherFragment = new CityWeatherFragment();
                Bundle bundle = new Bundle();
                //Log.d("test7", "onCreate: "+cityList.get(i));
                //Log.d("test7", "onCreate: "+weatherIdList.get(i));
                //bundle.putString("countyName",cityList.get(i));
                //cityWeatherFragment.setArguments(bundle);
                fragmentList.add(cityWeatherFragment);
            }
            //Log.d("test7", "onCreate: "+fragmentList);
        imageViewList.clear();
        pointLayout.removeAllViews();   //将布局当中所有元素全部移除
        initPoint();
    }

    protected void onRestart(){
        super.onRestart();
        cityList.clear();
        fragmentList.clear();
        initPager();
        pagerAdapter.notifyDataSetChanged();
        //imageViewList.clear();
        //pointLayout.removeAllViews();   //将布局当中所有元素全部移除
        //initPoint();
        viewPager.setCurrentItem(currentPos);
        closeProgressDialog();
    }
/*弹出式菜单*/
private void showPopupMenu(View view) {
    PopupMenu popupMenu = new PopupMenu(WeatherActivity.this, view);
    popupMenu.getMenuInflater().inflate(R.menu.new_menu, popupMenu.getMenu());

    popupMenu.setOnMenuItemClickListener(item -> {
        int itemId = item.getItemId();

        if (itemId == R.id.add_city) {
            startActivity(new Intent(WeatherActivity.this, CityManagerActivity.class));
            return true;
        }
        else if (itemId == R.id.setting) {
            startActivityForResult(new Intent(WeatherActivity.this, SettingActivity.class), 1);
            return true;
        }
        return false;
    });

    popupMenu.show();
}

    private void initPoint() {
//        创建小圆点 ViewPager页面指示器的函数
        if (fragmentList.size()!=0) {
            for (int i = 0; i < fragmentList.size(); i++) {
                ImageView pIv = new ImageView(this);
                pIv.setImageResource(R.mipmap.point1);
                pIv.setLayoutParams(new LinearLayout.LayoutParams(40, LinearLayout.LayoutParams.WRAP_CONTENT));
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) pIv.getLayoutParams();
                lp.setMargins(0, 0, 20, 0);
                imageViewList.add(pIv);
                pointLayout.addView(pIv);
            }
            imageViewList.get(0).setImageResource(R.mipmap.point2);
        }
    }
    /*
    请求天气数据
     */
    public void requestWeather(String weatherId)
    {
        showProgressDialog("正在加载天气中...");
        mCityName = weatherId;
        //Log.d("test2", weatherId);
        //Log.d("test2", mCityName);
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=6f6b5169b08547f483d662d4e8c5d591";
        //String weatherUrl = "https://api.heweather.net/s6/weather?location="+weatherId+"&key=0ae600dd9d7143c79e56f1fd69aec8a5";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败hahaha",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseText = response.body().string();
                //Log.d("test3", responseText);
                final Weather weather = Utility.handleWeatherResponse(responseText);
                String weatherInfo = weather.now.more.info;
                //Log.d("test66", "onResponse: "+weatherInfo);
                int code = picCode(weatherInfo);
                //Log.d("test66", "onResponse: "+code);
                //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                if (!isSameCode(code)){
                    editor = prefs.edit();
                    editor.putInt("code",code);
                    editor.apply();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather != null && "ok".equals(weather.status))
                        {

                            CityWeatherFragment cwFrag = fragmentList.get(viewPager.getCurrentItem());
                            cwFrag.showWeatherInfo(weather);
                            //臭bug耽误我一天：
                            // CityWeatherFragment cwFrag = (CityWeatherFragment) getSupportFragmentManager().findFragmentById(R.id.weather_pager);
                            //Log.d("test44", "run: "+weather.basic.cityName);
                        }
                        else
                        {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败nonono",Toast.LENGTH_SHORT).show();
                        }

                        loadBingPic();
                        swipeRefresh.setRefreshing(false);
                        closeProgressDialog();
                    }
                });
            }
        });

    }
    /*判断当前页天气是否变化*/
    private boolean isSameCode(int new_code){
        int old_code = prefs.getInt("code",-1);
        if (old_code != new_code){
            return false;
        }else{
            return true;
        }
    }
    /*将不同类型天气转换成int型*/
    private int picCode(String weatherInfo){
        int weatherCode = -1;
        //Log.d("test77", "picCode: "+weatherInfo);
        if ("晴".equals(weatherInfo) ){
            weatherCode = 0;
        }else if ("阴".equals(weatherInfo) || "多云".equals(weatherInfo)){
            weatherCode = 1;
        }else if("雨".equals(weatherInfo) || "小雨".equals(weatherInfo) || "阵雨".equals(weatherInfo) || "中雨".equals(weatherInfo)
        || "大雨".equals(weatherInfo)|| "雷阵雨".equals(weatherInfo)){
            weatherCode = 2;
        }
        //Log.d("test77", "picCode: "+weatherCode);
        return weatherCode;
    }
    /*根据天气加载图片*/
    private void loadBingPic(){
        int weather_code = prefs.getInt("code",-1);
        int old_Code = prefs.getInt("oldcode",-1);
        int index=prefs.getInt("index",0);
        //Log.d("test55", "loadBingPic: "+weather_code);
        if(weather_code != -1 && weather_code != prefs.getInt("oldcode",-1) && prefs2.getBoolean("isBgChange",true)){
            Random rand = new Random();
            index = rand.nextInt(bg_images[weather_code].length);
            Glide.with(this).load(bg_images[weather_code][index]).into(bingPicImg);
            editor = prefs.edit();
            editor.putInt("oldcode",weather_code);
            editor.putInt("index",index);
            editor.apply();
        }
        else {
            Glide.with(this).load(bg_images[old_Code][index]).into(bingPicImg);
        }
    }

    private void showProgressDialog(String msg){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(msg);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

}
