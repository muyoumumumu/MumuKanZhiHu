package com.muyoumumumu.mumukanzhihu.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.muyoumumumu.mumukanzhihu.R;
import com.muyoumumumu.mumukanzhihu.adapter.CommentsAdapter;
import com.muyoumumumu.mumukanzhihu.bean.ZhiHuComment;
import com.muyoumumumu.mumukanzhihu.other.Api;
import com.muyoumumumu.mumukanzhihu.other.DividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * zhihu_comments
 * Created by amumu on 2016/9/17.
 */
public class CommentsActivity extends AppCompatActivity {

    private Toolbar toolBar;
    private String id;
    private RecyclerView recycler_view;
    private List<ZhiHuComment> list=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhihu_comments);

        init();

        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //获取长评论
        JsonObjectRequest request=new JsonObjectRequest(Api.COMMENTS + id + "/long-comments", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("comments");
                    if (jsonArray.length()==0){
                        Snackbar.make(recycler_view,"暂时没有长评论",Snackbar.LENGTH_SHORT).show();
                    }else {
                        String author,content,avatar,time;
                        int likes;
                        JSONObject object;
                        for (int i = jsonArray.length()-1;i >= 0 ;i--){
                            object=jsonArray.getJSONObject(i);
                            author=object.getString("author");
                            content=object.getString("content");
                            avatar=object.getString("avatar");
                            time=object.getString("time");
                            likes=object.getInt("likes");
                            ZhiHuComment zhihuComment=new ZhiHuComment(author,content,avatar,time,likes);
                            //短
                            list.add(zhihuComment);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Snackbar.make(recycler_view,"加载错误",Snackbar.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(CommentsActivity.this.getApplicationContext()).add(request);

        //获取短评论
        JsonObjectRequest request2=new JsonObjectRequest(Api.COMMENTS + id + "/short-comments", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("comments");
                    if (jsonArray.length()==0){
                        Snackbar.make(recycler_view,"暂时没有评论",Snackbar.LENGTH_SHORT).show();
                    }else {
                        String author,content,avatar,time;
                        int likes;
                        JSONObject object;
                        ZhiHuComment zhihuComment;
                        for (int i = jsonArray.length()-1;i >= 0 ;i--){
                            object=jsonArray.getJSONObject(i);
                            author=object.getString("author");
                            content=object.getString("content");
                            avatar=object.getString("avatar");
                            time=object.getString("time");
                            likes=object.getInt("likes");
                            zhihuComment=new ZhiHuComment(author,content,avatar,time,likes);
                            //长
                            list.add(zhihuComment);
                        }
                    }

                    CommentsAdapter commentsAdapter=new CommentsAdapter(CommentsActivity.this,list);
                    recycler_view.setAdapter(commentsAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Snackbar.make(recycler_view,"加载错误",Snackbar.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(CommentsActivity.this.getApplicationContext()).add(request2);

        /*RequestQueue queue=Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
        queue.add(request2);*/



    }

    private void init() {
        toolBar = (Toolbar) findViewById(R.id.tool_bar);

        //?????
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new LinearLayoutManager(CommentsActivity.this));
        recycler_view.addItemDecoration(new DividerItemDecoration(CommentsActivity.this,LinearLayoutManager.VERTICAL));

        id = getIntent().getStringExtra("id");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
