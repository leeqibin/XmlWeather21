package com.example.administrator.xmlweather21;

/**
 * Created by Administrator on 2017/12/27.
 */

public class WeatherInf {
    String date;
    String high;
    String low;
    M day;
    M night;
}
class M{
    String type;
    String fengxiang;
    String fengli;
    public String inf(){
        String str = type + "风向：" + fengxiang + "风力：" + fengli;
        return str;
    }
}
