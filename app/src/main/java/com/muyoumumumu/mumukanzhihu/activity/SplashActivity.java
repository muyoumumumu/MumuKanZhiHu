package com.muyoumumumu.mumukanzhihu.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.muyoumumumu.mumukanzhihu.R;
import com.muyoumumumu.mumukanzhihu.other.Api;
import com.muyoumumumu.mumukanzhihu.other.NetworkState;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by amumu on 2016/10/13.
 * welcome
 */

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences sp;
    private RequestQueue queue;
    private TextView name;
    private ImageView img;

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences("user_settings", MODE_PRIVATE);
        if (!sp.getBoolean("not_load_splash", true)) {
            setContentView(R.layout.splash_activity);
            init();
            //是否有网络连接
            if (NetworkState.networkConnected(SplashActivity.this)) {
                //有网
                queue = Volley.newRequestQueue(getApplication());
                JsonObjectRequest request = new JsonObjectRequest(Api.START_IMAGE, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        try {
                            //isNull对象为空，找不到对象。isEmpty对象值为空。
                            if (jsonObject.isNull("img") || jsonObject.getString("img").isEmpty()) {
                                //设置为上一次的图像
                                img.setImageResource(R.drawable.welcome_img);
                                name.setText("欢迎来到 知乎日报");
                            } else {
                                Glide.with(SplashActivity.this)
                                        .load(jsonObject.getString("img"))
                                        .into(img);
                                name.setText(jsonObject.getString("text"));
                                //将图像缓存

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
                queue.add(request);
            } else {
                //没网
                img.setImageResource(R.drawable.welcome_img);
                name.setText("欢迎来到 知乎日报");
            }

            //设置延迟3秒进入主界面
            Timer timer = new Timer();
            final Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    startActivity(intent);
                }
            };
            timer.schedule(timerTask, 2000);

        } else {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }


    }

    private void init() {
        name = (TextView) findViewById(R.id.welcome_name);
        img = (ImageView) findViewById(R.id.welcome_img);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
