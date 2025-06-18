package com.example.coolweather.citymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.coolweather.R;
import com.example.coolweather.db.DBManager;

import java.util.ArrayList;
import java.util.List;


/*批量删除城市*/
public class DeleteCityActivity extends AppCompatActivity implements View.OnClickListener {
    Button back_bt, save_bt;
    ListView deleteLv;
    List<String> mDatas;
    List<String> deleteCitys;
    private DeleteCityAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 修复：添加完整的布局资源引用
        setContentView(R.layout.activity_delete_city);

        // 修复：添加空安全检查
        if (findViewById(R.id.city_delete_back) == null) {
            throw new RuntimeException("布局文件中缺少 city_delete_back 视图");
        }

        back_bt = findViewById(R.id.city_delete_back);
        save_bt = findViewById(R.id.city_delete_save);
        deleteLv = findViewById(R.id.delete_lv);

        DBManager dbManager_citylist = new DBManager();
        mDatas = dbManager_citylist.findAllCityName();
        deleteCitys = new ArrayList<>();

        back_bt.setOnClickListener(this);
        save_bt.setOnClickListener(this);

        adapter = new DeleteCityAdapter(this, mDatas, deleteCitys);
        deleteLv.setAdapter(adapter);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.city_delete_back) {
            if (deleteCitys.size() != 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示信息").setMessage("您尚未保存，是否保存删除结果？")
                        .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (int i = 0; i < deleteCitys.size(); i++) {
                                    String city = deleteCitys.get(i);
                                    DBManager dbManager = new DBManager();
                                    dbManager.deleteCityInfo(city);
                                }
                                finish();
                            }
                        });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.create().show();
            } else {
                finish();
            }
        } else if (id == R.id.city_delete_save) {
            for (int i = 0; i < deleteCitys.size(); i++) {
                String city = deleteCitys.get(i);
                DBManager dbManager = new DBManager();
                dbManager.deleteCityInfo(city);
            }
            finish();
        }
    }
}