package com.dzh.sunbin.shanghaibus;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private ImageButton btn_history;
    private PopupWindow popupwindow;
    private static String TAG = "MainActivity";
    SharedPreferences.Editor editor;
    SharedPreferences sp;
    List<Bus> buses = new ArrayList<Bus>();

    WebView webView;
    String url_home = "http://xxbs.sh.gov.cn:8080/weixinpage/index.html";
    String url = url_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        initObject();

        btn_history = (ImageButton)findViewById(R.id.history);
        btn_history.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                if (popupwindow != null&&popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    return;
                } else {
                    initmPopupWindowView();
                    popupwindow.showAsDropDown(v, 0, 15);
                }
            }
        });

        webView = (WebView)findViewById(R.id.web_view);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        Log.d(TAG, url);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, url);
                try {
                    java.net.URLDecoder urlDecoder = new java.net.URLDecoder();
                    String url_decode_UTF8 = urlDecoder.decode(url, "UTF-8");
                    Log.d(TAG, url_decode_UTF8);
                    java.net.URL obj_url = new java.net.URL(url_decode_UTF8);
                    String queryurl = obj_url.getQuery();
                    String[] querys = queryurl.split("&");
                    for (String query : querys) {
                        String[] kv = query.split("=");
                        if (kv.length == 2 && kv[0].compareTo("name") == 0) {
                            addBuses(kv[1], url);
                            break;
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
                view.loadUrl(url);
                return true;
            }
        });
    }

    private void initObject(){
        //获取SharedPreferences对象
        Context ctx = MainActivity.this;
        sp = ctx.getSharedPreferences("SP", MODE_PRIVATE);
        //存入数据
        editor = sp.edit();
        initBuses();
    }

    private void initBuses(){
        buses.clear();
        buses.add(new Bus("首页", url_home));
        Map<String,?> busmap = sp.getAll();
        Log.d(TAG, busmap.toString());
        for(String key:busmap.keySet()){
            buses.add(new Bus(key, (String)busmap.get(key)));
        }
    }

    private void saveBuses(){
        Log.d(TAG, "saveBuses");
        for(Bus bus:buses){
            if (bus.GetName().compareTo("首页") == 0){
                continue;
            }
            editor.putString(bus.GetName(), bus.GetUrl());
        }
        editor.commit();
    }

    private void addBuses(String name, String url){
        boolean needadd = true;
        for (Bus bus : buses) {
            if (bus.GetName().compareTo(name) == 0) {
                needadd = false;
            }
        }
        if (needadd) {
            if (buses.size() < 4) {
                buses.add(new Bus(name, url));
            } else {
                buses.get(1).SetName(buses.get(2).GetName());
                buses.get(1).SetUrl(buses.get(2).GetUrl());
                buses.get(2).SetName(buses.get(3).GetName());
                buses.get(2).SetUrl(buses.get(3).GetUrl());
                buses.get(3).SetName(name);
                buses.get(3).SetUrl(url);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        saveBuses();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about) {
            Toast.makeText(this, R.string.info, Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initmPopupWindowView() {
        // // 获取自定义布局文件pop.xml的视图
        LinearLayout customView = (LinearLayout) getLayoutInflater().inflate(R.layout.pophistory,
                null, false);
        ListView listView = (ListView) customView.findViewById(R.id.list_buses);
        BusesAdapter adapter = new BusesAdapter(MainActivity.this, R.layout.busitem, buses);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Bus bus = buses.get(position);
                url = bus.GetUrl();
                if (popupwindow != null&&popupwindow.isShowing())
                {
                    popupwindow.dismiss();
                }
                webView.loadUrl(url);
            }
        });


        // 创建PopupWindow实例,200,150分别是宽度和高度
        popupwindow = new PopupWindow(customView, dip2px(this, 165), dip2px(this, buses.size() * 41));

        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        //popupwindow.setAnimationStyle(R.style.AnimationFade);
        // 自定义view添加触摸事件
        customView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    popupwindow = null;
                }

                return false;
            }
        });
    }

    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }
}