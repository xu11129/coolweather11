package com.example.coolweather.citymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.coolweather.R;
import com.example.coolweather.db.Favorite;

import java.util.List;

public class CityManagerAdapter extends BaseAdapter {
    Context context;
    List<Favorite> myCity;
    public CityManagerAdapter(Context context,List<Favorite> datas){
        this.context = context;
        this.myCity = datas;
    }
    public int getCount(){
        if(myCity !=null){
            return myCity.size();
        }else{
            return 0;
        }
    }
    public Object getItem(int position){
        return myCity.get(position);
    }
    public long getItemId(int position){
        return position;
    }
    public View getView(int position , View convertView, ViewGroup parent){
        ViewHolder holder =null;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_citymanager,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        Favorite bean = myCity.get(position);
        holder.cityTv.setText(bean.getCountyName());
        return convertView;
    }


    class ViewHolder{
        TextView cityTv;
        public ViewHolder(View itemView){
            cityTv = itemView.findViewById(R.id.item_city_tv_city);

        }
    }
}
