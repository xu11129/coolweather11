package com.example.coolweather.util;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.io.Serializable;

public class SettingInfo implements Parcelable {
    private boolean isAllowUpdate;
    private int update;
    private boolean isBGchange;

    public SettingInfo(){}
    public SettingInfo(boolean isAllowUpdate,int update,boolean isBGchange){
        this.isAllowUpdate=isAllowUpdate;
        this.update=update;
        this.isBGchange = isBGchange;
    }

    public int getUpdate() {
        return update;
    }

    public boolean isAllowUpdate() {
        return isAllowUpdate;
    }

    public void setAllowUpdate(boolean allowUpdate) {
        this.isAllowUpdate = allowUpdate;
    }

    public void setUpdate(int update) {
        this.update = update;
    }

    public boolean getBGchange(){
        return isBGchange;
    }
    public void setBGchange(boolean isBGchange){
        this.isBGchange=isBGchange;
    }

    public int describeContents(){
        return 0;
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void writeToParcel(Parcel out, int flag){
        out.writeBoolean(isAllowUpdate);
        out.writeInt(update);
        out.writeBoolean(isBGchange);
    }
    public static final Parcelable.Creator<SettingInfo> CREATOR=new Creator<SettingInfo>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public SettingInfo createFromParcel(Parcel parcel) {
            SettingInfo settingInfo = new SettingInfo();
            settingInfo.setAllowUpdate(parcel.readBoolean());
            settingInfo.setUpdate(parcel.readInt());
            settingInfo.setBGchange(parcel.readBoolean());
            return settingInfo;
        }

        @Override
        public SettingInfo[] newArray(int i) {
            return new SettingInfo[i];
        }
    };

}
