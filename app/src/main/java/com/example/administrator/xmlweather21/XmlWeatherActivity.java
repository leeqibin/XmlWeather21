package com.example.administrator.xmlweather21;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.widget.LinearLayout.LayoutParams;

public class XmlWeatherActivity extends AppCompatActivity{
    HttpURLConnection httpURLConnection = null;
    ArrayList<WeatherInf> weatherInfs = new ArrayList<>();
    String cityname = "广州";
    private AutoCompleteTextView mCityname;
    private Button mFind;
    private LinearLayout mShowTV;

    private String weburl = "http://10.0.2.2/phptest/area_api.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xml_weather);
        setTitle("天气查询XML");
        GetArea getArea = new GetArea();
        getArea.start();
        mCityname = (AutoCompleteTextView) findViewById(R.id.cityname);
        mFind = (Button) findViewById(R.id.search);
        mShowTV = (LinearLayout) findViewById(R.id.show_weather);
        mFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowTV.removeAllViews();
                cityname = mCityname.getText().toString();
                Toast.makeText(XmlWeatherActivity.this,"正在查询天气信息...",Toast.LENGTH_LONG).show();
                GetXml gx = new GetXml(cityname);
                gx.start();
            }
        });
    }
    private final Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case 1:
                    show();
                    break;
                case 2:
                    ArrayAdapter adapter = (ArrayAdapter)msg.obj;
                    mCityname.setAdapter(adapter);
                    mCityname.setThreshold(1);

            }
            super.handleMessage(msg);
        }
    };
    class GetArea extends Thread{
        private String area="";
        public GetArea(){
            try {
                this.area = URLEncoder.encode(area,"UTF-8");
            }catch (Exception ee){

            }
        }
        @Override
        public void run() {
            try {
                URL url = new URL(weburl);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setConnectTimeout(5000);
                int code = httpURLConnection.getResponseCode();
                if(code==200){
                    InputStream in = httpURLConnection.getInputStream();
                    InputStreamReader din = new InputStreamReader(in);
                    BufferedReader bdin = new BufferedReader(din);
                    StringBuffer sbf = new StringBuffer();
                    String line = null;

                    while((line=bdin.readLine())!=null){
                        sbf.append(line);
                    }

                    String jsonData =new String(sbf.toString().getBytes(),"UTF-8") ; //此句非常重要！把字符串转为utf8编码，因为String 默认是unicode编码的。
                    JSONArray jsonArray = new JSONArray(jsonData);
                    List<String> list = new ArrayList<String>();
                    for(int i=0;i<jsonArray.length();i++){
                        String pro = jsonArray.opt(i).toString();
                        list.add(pro);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(XmlWeatherActivity.this,android.R.layout.simple_spinner_dropdown_item,list);
                    Message msg = new Message();
                    msg.obj = adapter;
                    msg.what = 2;
                    handler.sendMessage(msg);

                    //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // mCityname.setAdapter(adapter);
                    // sp_province.setAdapter(adapter);//线程不能访问主线程activity的控件
                }else{
                    Looper.prepare();
                    Toast.makeText(XmlWeatherActivity.this,"网址不可访问",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }catch (Exception ee){
                Looper.prepare();
                Toast.makeText(XmlWeatherActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }
    }

    class GetXml extends Thread{
        private String urlstr =  "http://wthrcdn.etouch.cn/WeatherApi?city=";
        public GetXml(String cityname){
            try{
                urlstr = urlstr+ URLEncoder.encode(cityname,"UTF-8");
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }

        @Override
        public void run() {
            InputStream din = null;
            try{
                URL url = new URL(urlstr);
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                din = httpURLConnection.getInputStream();
                XmlPullParser xmlPullParser = Xml.newPullParser();
                xmlPullParser.setInput(din,"UTF-8");
                WeatherInf pw = null;
                M m = null;
                int eveType = xmlPullParser.getEventType();
                while(eveType != XmlPullParser.END_DOCUMENT){
                    //开始节点
                    if(eveType == XmlPullParser.START_TAG){
                        String tag = xmlPullParser.getName();
                        if(tag.equalsIgnoreCase("weather")){
                            pw = new WeatherInf();
                        }
                        //下个节点
                        if(tag.equalsIgnoreCase("date")){
                            if(pw != null){
                                pw.date = xmlPullParser.nextText();
                            }
                        }
                        //下一个节点，以此类推
                        if(tag.equalsIgnoreCase("high")){
                            if(pw != null){
                                pw.high = xmlPullParser.nextText();
                            }
                        }
                        if(tag.equalsIgnoreCase("low")){
                            if(pw != null){
                                pw.low = xmlPullParser.nextText();
                            }
                        }
                        if(tag.equalsIgnoreCase("day")){
                            m = new M();
                        }
                        if(tag.equalsIgnoreCase("night")){
                            m = new M();
                        }
                        if(tag.equalsIgnoreCase("type")){
                            if(m != null){
                                m.type = xmlPullParser.nextText();
                            }
                        }
                        if(tag.equalsIgnoreCase("fengxiang")){
                            if(m != null){
                                m.fengxiang = xmlPullParser.nextText();
                            }
                        }
                        if(tag.equalsIgnoreCase("fengli")){
                            if(m != null){
                                m.fengli = xmlPullParser.nextText();
                            }
                        }
                    }
                    //后节点
                    else if(eveType == XmlPullParser.END_TAG){
                        String tag = xmlPullParser.getName();
                        if (tag.equalsIgnoreCase("weather")){
                            weatherInfs.add(pw);
                            pw = null;
                        }
//                        if(tag.equalsIgnoreCase("date")){
//                            pw.day = m;
//                            m = null;
//                        }
                        if(tag.equalsIgnoreCase("day")){
                            pw.day = m;
                            m = null;
                        }
                        if(tag.equalsIgnoreCase("night")){
                            pw.night = m;
                            m = null;
                        }
                    }
                    eveType = xmlPullParser.next();
                }
                //信使传值
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    public void show(){
        //显示
        mShowTV.removeAllViews();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        for(int i = 0; i<weatherInfs.size();i++){
            //日期
            TextView dateView = new TextView(this);
            dateView.setGravity(Gravity.CENTER_HORIZONTAL);
            dateView.setLayoutParams(params);
            dateView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            dateView.setText("日期："+weatherInfs.get(i).date);
            mShowTV.addView(dateView);
            //白天和夜间
            TextView mView = new TextView(this);
            mView.setLayoutParams(params);
            String str = "高温：" + weatherInfs.get(i).high+",低温：" + weatherInfs.get(i).low + "\n";
            str = str + "白天：" + weatherInfs.get(i).day.inf() + "\n";
            str = str + "夜间：" +weatherInfs.get(i).night.inf();
            mView.setText(str);
            mShowTV.addView(mView);
        }

    }

}
