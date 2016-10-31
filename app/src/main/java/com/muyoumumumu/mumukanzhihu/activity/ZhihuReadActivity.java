package com.muyoumumumu.mumukanzhihu.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.muyoumumumu.mumukanzhihu.R;
import com.muyoumumumu.mumukanzhihu.other.Api;
import com.muyoumumumu.mumukanzhihu.other.MyDbHelper;
import com.muyoumumumu.mumukanzhihu.other.NetworkState;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 知乎查看界面
 * Created by amumu on 2016/8/24.
 */
public class ZhihuReadActivity extends AppCompatActivity {

//    private RecyclerView recyclerView;
//    private StaggeredGridLayoutManager layoutManager;
//    private MyTestAdapter myTestAdapter;
    private AppBarLayout appBarLayout;
    private net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout toolBarLayout;
    private Toolbar toolBar;
    private SharedPreferences sp;
    private AlertDialog dialog;
    private ImageView firstImage;
    private TextView tv_CopyRight;
    private FloatingActionButton fab;
    private WebView web_view;

    private String id;
    private RequestQueue queue;
    //分享链接
    private String shareUrl;
    private int comments;
    private int likes;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        //theme
        setTheme(R.style.DayTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhihu_read_activity);

        firstImage = (ImageView) findViewById(R.id.image_view);
        tv_CopyRight = (TextView) findViewById(R.id.text_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        appBarLayout = (AppBarLayout) findViewById(R.id.appBar_layout);

        toolBarLayout = (net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout) findViewById(R.id.toolBar_layout);
//        toolBarLayout.setTitle("w bushi 标题");
//        toolBarLayout.setCollapsedTitleTextColor(Color.WHITE);
//        toolBarLayout.setExpandedTitleColor(Color.WHITE);

        //设置返回
        toolBar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sp = getSharedPreferences("user_settings", MODE_PRIVATE);

        dialog = new AlertDialog.Builder(ZhihuReadActivity.this).create();
        dialog.setView(getLayoutInflater().inflate(R.layout.loading_zhihureading, null));
//        dialog.show();

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        final String title = intent.getStringExtra("title");
        //????
        final String first_image = intent.getStringExtra("first_image");

        toolBarLayout.setTitle(title);
        //还有样式没写
        toolBarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        toolBarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        toolBarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBarPlus1);
        toolBarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPlus1);

        //队列
        queue = Volley.newRequestQueue(getApplicationContext());

        web_view = (WebView) findViewById(R.id.web_view);
        //视图不会滚动时是否显示滚动条
        web_view.setScrollbarFadingEnabled(true);
        web_view.getSettings().setJavaScriptEnabled(true);
        //缩放,设置为不能缩放可以防止页面上出现放大和缩小的图标
        web_view.getSettings().setBuiltInZoomControls(true);
        //不设置缓存，从网络加载
        web_view.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //开启DOM storage API功能
        web_view.getSettings().setDomStorageEnabled(true);
        //关闭Application Cache的功能
        web_view.getSettings().setAppCacheEnabled(false);

        //默认不用第三方浏览器打开
        if (sp.getBoolean("use_other_browser", false)) {
            //给浏览器网址
            web_view.setWebViewClient(new WebViewClient() {
                //暂时不知道怎么写
            });

            // 设置在本WebView内可以通过按下返回上一个html页面
            web_view.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    //获取动作
                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                        if (i == KeyEvent.KEYCODE_BACK && web_view.canGoBack()) {
                            web_view.goBack();
                        }
                    }
                    return true;
                }
            });

        }

        //是否加载图片，true 为不加载 false 为加载，Block 障碍
        //Gets whether the WebView does not load image resources from the network.
        web_view.getSettings().setBlockNetworkImage(sp.getBoolean("no_pic_mode", false));


        /* *如果没有网络连接
        * 则加载缓存中的内容
        * 如果有 则网络加载
        * */
        if (!NetworkState.networkConnected(ZhihuReadActivity.this)) {
            firstImage.setImageResource(R.drawable.background);
            firstImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

            //// body中替换掉img-place-holder div
            // 可以去除网页中div所占的区域
            // 如果没有去除这个div，那么整个网页的头部将会出现一部分的空白区域
            // 删除 <div class="img-place-holder">
            String content = loadContentFromDB(id).replace("<div class=\"img-place-holder\">", "");
            //继续删除 <div class="headline">
            content = content.replace("<div class=\"headline\">", "");

            //theme
            String theme = "<body className=\"\" onload=\"onLoaded()\">";

            //如果加载的数据有误为空，报错，不为空 则载入
            if (loadContentFromDB(id) == null || loadContentFromDB(id).isEmpty()) {
                Snackbar.make(fab, "加载失败啦！", Snackbar.LENGTH_SHORT).show();
            } else {
                String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/zhihu_daily.css\" type=\"text/css\">";

                String html = "<!DOCTYPE html>\n"
                        + "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                        + "<head>\n"
                        + "\t<meta charset=\"utf-8\" />"
                        + css
                        + "\n</head>\n"
                        + theme
                        + content
                        + "</body></html>";
                web_view.loadDataWithBaseURL("x-data://base", html, "text/html", "utf-8", null);
            }
            if (dialog.isShowing())
                dialog.dismiss();

        } else {
            //网络加载
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Api.NEWS + id, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        //// 需要注意的是这里有可能没有body。。。 好多坑。。。
                        // 如果没有body，则加载share_url中内容
                        if (jsonObject.isNull("body")) {
                            web_view.loadUrl(jsonObject.getString("share_url"));
                            firstImage.setImageResource(R.drawable.background);
                            firstImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

                            shareUrl = jsonObject.getString("shareUrl");
                        } else {
                            //有body情况
                            shareUrl = jsonObject.getString("share_url");

                            if (!jsonObject.isNull("image")) {
                                //图片加载库Glide
                                //.with(环境).load(图片地址).centerCrop(居中适应).into(注入到firstImage).
                                Glide.with(ZhihuReadActivity.this).load(jsonObject.getString("image"))
                                        .centerCrop().into(firstImage);
                                //image_source 图片来源（说明)??
                                tv_CopyRight.setText(jsonObject.getString("image_source"));
                            } else if (first_image == null) {
                                firstImage.setImageResource(R.drawable.background);
                                firstImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            } else {
                                //直接载入地址信息？？
                                Glide.with(ZhihuReadActivity.this).load(first_image).centerCrop().into(firstImage);
                            }

                            //css部分,采用本地css文件 而不是默认的网络加载
                            String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/zhihu_daily.css\" type=\"text/css\">";
                            //content部分
                            String content = jsonObject.getString("body").replace("<div class=\"img-place-holder\">", "");
                            content = content.replace("<div class=\"headline\">", "");

                            //theme
                            String theme = "<body className=\"\" onload=\"onLoaded()\">";

                            String html = "<!DOCTYPE html>\n"
                                    + "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                                    + "<head>\n"
                                    + "\t<meta charset=\"utf-8\" />"
                                    + css
                                    + "\n</head>\n"
                                    + theme
                                    + content
                                    + "</body></html>";

                            //log
                            Log.d("html", html);

                            web_view.loadDataWithBaseURL("x-data://base", html, "text/html", "utf-8", null);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Snackbar.make(fab,"zenmmle??/",Snackbar.LENGTH_LONG).show();
                    }

                    if (dialog.isShowing())
                        dialog.dismiss();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    Snackbar.make(fab, "加载失败", Snackbar.LENGTH_SHORT).show();

                    if (dialog.isShowing())
                        dialog.dismiss();
                }
            });

            queue.add(request);

        }

        //请求评论和赞
        JsonObjectRequest request_ex = new JsonObjectRequest(Request.Method.GET, Api.STORY_EXTRA + id, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                if (!jsonObject.isNull("comments")) {
                    try {
                        comments = jsonObject.getInt("comments");
                        likes = jsonObject.getInt("popularity");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Snackbar.make(fab, "加载错误", Snackbar.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Snackbar.make(fab, "加载错误", Snackbar.LENGTH_SHORT).show();
            }
        });

        queue.add(request_ex);

        //fab设置分享
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent shareIntent = new Intent();
                    //设置ACTION_SEND 动作，样式为text/plain
                    shareIntent.setAction(Intent.ACTION_SEND).setType("text/plain");
                    String shareText = title + " " + shareUrl + "分享自阿木木,hahaha";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                    startActivity(Intent.createChooser(shareIntent, "分享至"));
                } catch (ActivityNotFoundException e) {
                    Snackbar.make(fab, "又加载失败了！", Snackbar.LENGTH_SHORT).show();
                }
            }
        });


/*//        list = (ListView) findViewById(R.id.list);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        //瀑布流
        layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        myTestAdapter = new MyTestAdapter(getdate());

        recyclerView.setAdapter(myTestAdapter);*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_zhihu_read, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        String temp = "赞 " + likes;
        menu.findItem(R.id.action_likes).setTitle(temp);
        temp = "评论 " + comments;
        menu.findItem(R.id.action_comments).setTitle(temp);
        menu.findItem(R.id.action_open_in_browser).setTitle("在浏览器打开");

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_likes:
                break;
            case R.id.action_comments:
                //就是不想写
                startActivity(new Intent(ZhihuReadActivity.this,CommentsActivity.class).putExtra("id",this.id));
                break;
            case R.id.action_open_in_browser:
                //第三方浏览器打开，注意写法
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(Api.ZHIHU_DAILY_BASE_URL+this.id)));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*private String[] getdate() {
        int a = 30;
        String[] strings = new String[a];
        for (int i = 0; i < a; i++)
            strings[i] = String.valueOf(i);
        //可能list.add是积累到最后一起add的
        return strings;
    }*/

    private String loadContentFromDB(String id) {
        String content = null;
        MyDbHelper dbHelper = new MyDbHelper(ZhihuReadActivity.this, "HistoryPost.db", null, 1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Contents", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex("id")).equals(id))
                    content = cursor.getString(cursor.getColumnIndex("content"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return content;
    }

}

