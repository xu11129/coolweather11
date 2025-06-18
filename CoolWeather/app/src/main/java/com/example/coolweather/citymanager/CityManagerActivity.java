package com.example.coolweather.citymanager;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.coolweather.R;
import com.example.coolweather.WeatherActivity;
import com.example.coolweather.db.DBManager;
import com.example.coolweather.db.Favorite;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import com.example.coolweather.R;

public class CityManagerActivity extends AppCompatActivity implements View.OnClickListener{
    Button cm_delete,cm_back;
    FloatingActionButton cm_add;
    ListView cm_city;
    List<Favorite> myCity_data;
    private CityManagerAdapter adapter;
    public DrawerLayout drawerLayout;
    public SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            View decorView=getWindow().getDecorView();
            int option=View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_city_manager);
        cm_add = (FloatingActionButton) findViewById(R.id.city_manager_add);
        cm_back = (Button) findViewById(R.id.city_manager_back);
        cm_delete = (Button) findViewById(R.id.city_manager_delete);
        cm_city = findViewById(R.id.city_lv);
        myCity_data = new ArrayList<>();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout2);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh2);
        cm_add.setOnClickListener(this);
        cm_back.setOnClickListener(this);
        cm_delete.setOnClickListener(this);
        adapter= new CityManagerAdapter(this,myCity_data);
        cm_city.setAdapter(adapter);
        //列表子项目的点击事件
        cm_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                WeatherActivity.wactivity.finish();  //先结束上一个activity，否则容易出现重复跳转到管理界面
                Intent intent = new Intent(CityManagerActivity.this, WeatherActivity.class);
                intent.putExtra("cityName",position);
                startActivity(intent);
                finish();
            }
        });
        //长按事件
        cm_city.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(CityManagerActivity.this);
                    String del_city = myCity_data.get(position).getCountyName();
                    dialog.setTitle("删除城市");
                    dialog.setMessage("确定删除"+del_city+"吗？");
                    dialog.setIcon(R.mipmap.ic_launcher_foreground);
                    dialog.setPositiveButton("是的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DBManager dbManager_del = new DBManager();
                            dbManager_del.deleteCityInfo(del_city);
                            refreshCountyList();
                        }
                    });
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(CityManagerActivity.this,"您取消了删除操作。",Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.show();
                return true;
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                refreshCountyList();
            }
        });
    }
    protected void onResume(){
        super.onResume();
        refreshCountyList();
    }
    protected void onRestart(){
        super.onRestart();
        refreshCountyList();
    }
    //刷新界面
    public void refreshCountyList(){
        DBManager dbManager = new DBManager();
        List<Favorite> list =dbManager.favoriteList;
        myCity_data.clear();
        myCity_data.addAll(list);
        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.city_manager_add) {
            // 弹出碎片以选择城市
            drawerLayout.openDrawer(GravityCompat.START);
        } else if (id == R.id.city_manager_back) {
            finish();
        } else if (id == R.id.city_manager_delete) {
            // 跳转至批量删除城市界面
            Intent intent3 = new Intent(this, DeleteCityActivity.class);
            startActivity(intent3);
        }
    }
}