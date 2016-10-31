package com.muyoumumumu.mumukanzhihu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.muyoumumumu.mumukanzhihu.R;
import com.muyoumumumu.mumukanzhihu.adapter.MyAdapter;
import com.muyoumumumu.mumukanzhihu.other.Api;
import com.muyoumumumu.mumukanzhihu.other.NetworkState;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


public class MainActivity extends AppCompatActivity {


    private Toolbar toolBar;
    private TabLayout tabLayout;
    private net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout collapsingToolbarLayout ;
    private ImageView img;
    private ViewPager viewPager;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.DayTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolBar = (Toolbar) findViewById(R.id.tool_bar);
        //加设新的title
        toolBar.setTitle(R.string.app_name);
        //设置一个toolbar ，如果不设置会默认一个；
        setSupportActionBar(toolBar);

        //取消默认剧中的 title
        collapsingToolbarLayout= (net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitleEnabled(false);

        img= (ImageView) findViewById(R.id.img);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        //设置连接
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager(), MainActivity.this));
        //是有顺序的
        tabLayout.setupWithViewPager(viewPager);



        if (NetworkState.networkConnected(MainActivity.this)) {
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
                        } else {
                            Glide.with(MainActivity.this)
                                    .load(jsonObject.getString("img"))
                                    .into(img);
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
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //映射菜单
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            //进入设置页
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
        } else if (id == R.id.action_about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁缓存，真是时代好青年
        deleteDir(getCacheDir());
    }

    /**
     * 删除缓存
     **/
    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}

