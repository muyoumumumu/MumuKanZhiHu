package com.muyoumumumu.mumukanzhihu.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.muyoumumumu.mumukanzhihu.R;
import com.muyoumumumu.mumukanzhihu.other.Api;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * guokr_read
 * Created by amumu on 2016/10/22.
 */
public class GuokrReadActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private ImageView ivHeadline;
    private WebView wbMain;
    private net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout toolbarLayout;

    private AlertDialog dialog;

    private String id;
    private String headlineUrl;
    private String title;

    private String content;

    private SharedPreferences sp;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.DayTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhihu_read_activity);

        initViews();

        sp = getSharedPreferences("user_settings",MODE_PRIVATE);

        queue = Volley.newRequestQueue(getApplicationContext());

        dialog = new AlertDialog.Builder(GuokrReadActivity.this).create();
        dialog.setView(getLayoutInflater().inflate(R.layout.loading_zhihureading,null));

        id = getIntent().getStringExtra("id");
        headlineUrl = getIntent().getStringExtra("headlineImageUrl");
        title = getIntent().getStringExtra("title");

        setCollapsingToolbarLayoutTitle(title);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent shareIntent = new Intent().setAction(Intent.ACTION_SEND).setType("text/plain");
                    String shareText = title + " " +  Api.GUOKR_ARTICLE_LINK_V1 + id + getString(R.string.share_extra);
                    shareIntent.putExtra(Intent.EXTRA_TEXT,shareText);
                    startActivity(Intent.createChooser(shareIntent,getString(R.string.share_to)));
                } catch (android.content.ActivityNotFoundException ex){
                    Snackbar.make(fab,"加载失败",Snackbar.LENGTH_SHORT).show();
                }

            }
        });

        if (headlineUrl != null){
            Glide.with(GuokrReadActivity.this)
                    .load(headlineUrl)
                    .asBitmap()
                    .centerCrop()
                    .into(ivHeadline);
        } else {
            ivHeadline.setImageResource(R.drawable.background);
        }

        // 设置是否加载图片，true不加载，false加载图片sp.getBoolean("no_picture_mode",false)
        wbMain.getSettings().setBlockNetworkImage(sp.getBoolean("no_picture_mode",false));

        //能够和js交互
        wbMain.getSettings().setJavaScriptEnabled(true);
        //缩放,设置为不能缩放可以防止页面上出现放大和缩小的图标
        wbMain.getSettings().setBuiltInZoomControls(false);
        //缓存
        wbMain.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //开启DOM storage API功能
        wbMain.getSettings().setDomStorageEnabled(true);
        //开启application Cache功能
        wbMain.getSettings().setAppCacheEnabled(false);

        if (sp.getBoolean("in_app_browser",false)){
            //不调用第三方浏览器即可进行页面反应
            wbMain.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    wbMain.loadUrl(url);
                    return true;
                }

            });

            // 设置在本WebView内可以通过按下返回上一个html页面
            wbMain.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN){
                        if (keyCode == KeyEvent.KEYCODE_BACK && wbMain.canGoBack()){
                            wbMain.goBack();
                            return true;
                        }
                    }
                    return false;
                }
            });
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Api.GUOKR_ARTICLE_BASE_URL + "?pick_id=" + id, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.getString("ok").equals("true")){

                        {

                            content = jsonObject.getJSONArray("result").getJSONObject(0).getString("content");

                            String parseByTheme =  "<div class=\"article\" id=\"contentMain\">"
                                        + "<div class=\"content\" id=\"articleContent\" >";


                            String css = "\n<link rel=\"stylesheet\" href=\"file:///android_asset/guokr_master.css\" />\n";

                            String html = "<!DOCTYPE html>\n"
                                    + "<html>\n"
                                    + "<head>\n"
                                    + "\t<meta charset=\"utf-8\" />"
                                    + css
                                    + "\n</head>"
                                    + "<body>"
                                    + parseByTheme
                                    + content
                                    + "</div></div>"
                                    +"<script>\n"
                                    + "var ukey = null;\n"
                                    + "</script>\n"
                                    + "<script src=\"file:///android_asset/guokr.base.js\"></script>\n"
                                    + "<script src=\"file:///android_asset/guokr.articleInline.js\"></script>"
                                    + "</body></html>";

                            wbMain.loadDataWithBaseURL("x-data://base",html,"text/html","utf-8",null);

                        }
                    }

                    if (dialog.isShowing()){
                        dialog.dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                Snackbar.make(fab,"加载失败",Snackbar.LENGTH_SHORT).show();

                if (dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        });

        queue.add(request);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_read,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }

        if (item.getItemId() == R.id.action_open_in_browser){
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(Api.GUOKR_ARTICLE_LINK_V1 + id)));
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews() {

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        toolbarLayout = (net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout) findViewById(R.id.toolBar_layout);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivHeadline = (ImageView) findViewById(R.id.image_view);
        wbMain = (WebView) findViewById(R.id.web_view);

    }

    // to change the title's font size of toolbar layout
    private void setCollapsingToolbarLayoutTitle(String title) {
        toolbarLayout.setTitle(title);
        toolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        toolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        toolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBarPlus1);
        toolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPlus1);
    }

}
