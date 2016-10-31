package com.muyoumumumu.mumukanzhihu.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.muyoumumumu.mumukanzhihu.R;
import com.muyoumumumu.mumukanzhihu.activity.GuokrReadActivity;
import com.muyoumumumu.mumukanzhihu.adapter.GuokrPostAdapter;
import com.muyoumumumu.mumukanzhihu.bean.GuokrPost;
import com.muyoumumumu.mumukanzhihu.other.Api;
import com.muyoumumumu.mumukanzhihu.other.DividerItemDecoration;
import com.muyoumumumu.mumukanzhihu.other.OnRecyclerViewOnClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * guoke.
 * Created by amumu on 2016/10/22.
 */
public class GuokrFragment extends Fragment {

    private RecyclerView recyclerview_guokr;
    private SwipeRefreshLayout refresh_guokr;
    private ArrayList<GuokrPost> guokr_list = new ArrayList<>();

    private GuokrPostAdapter adapter;

    private static final String TAG = "GUOKR";

    // require an empty constructor
    public GuokrFragment(){

    }

    public static GuokrFragment newInstance() {
        return new GuokrFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_guokr,container,false);

        initViews(view);

        requestData();

        refresh_guokr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (!guokr_list.isEmpty()){
                    guokr_list.clear();
                }

                requestData();

            }
        });

        return view;
    }

    private void initViews(View view) {

        recyclerview_guokr = (RecyclerView) view.findViewById(R.id.rv_guokr_handpick);
        recyclerview_guokr.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerview_guokr.addItemDecoration(new DividerItemDecoration(getActivity(),LinearLayoutManager.VERTICAL));
        refresh_guokr = (SwipeRefreshLayout) view.findViewById(R.id.refresh_guokr);

        //设置下拉刷新的按钮的颜色
        refresh_guokr.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        //设置手指在屏幕上下拉多少距离开始刷新
        refresh_guokr.setDistanceToTriggerSync(300);
        //设置下拉刷新按钮的背景颜色
        refresh_guokr.setProgressBackgroundColorSchemeColor(Color.WHITE);
        //设置下拉刷新按钮的大小
        refresh_guokr.setSize(SwipeRefreshLayout.DEFAULT);
    }

    private void requestData(){

        refresh_guokr.post(new Runnable() {
            @Override
            public void run() {
                refresh_guokr.setRefreshing(true);
            }
        });

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Api.GUOKR_ARTICLES, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                try {
                    if (jsonObject.getString("ok").equals("true")){
                        JSONArray array = jsonObject.getJSONArray("result");
                        for (int i = 0; i < array.length(); i++){
                            JSONObject o = array.getJSONObject(i);
                            GuokrPost item = new GuokrPost(
                                    o.getString("id"),
                                    o.getString("title"),
                                    o.getString("headline_img_tb"),
                                    o.getString("summary"));

                            guokr_list.add(item);
                        }
                    }

                    if (guokr_list.size() != 0){

                        adapter = new GuokrPostAdapter(getActivity(), guokr_list);
                        adapter.setItemClickListener(new OnRecyclerViewOnClickListener() {
                            @Override
                            public void OnItemClick(View v, int position) {

                                Intent intent = new Intent(getActivity(),GuokrReadActivity.class);
                                intent.putExtra("id", guokr_list.get(position).getId());
                                intent.putExtra("headlineImageUrl", guokr_list.get(position).getImg());
                                intent.putExtra("title", guokr_list.get(position).getTitle());

                                startActivity(intent);
                            }
                        });

                        recyclerview_guokr.setAdapter(adapter);

                        refresh_guokr.post(new Runnable() {
                            @Override
                            public void run() {
                                refresh_guokr.setRefreshing(false);
                            }
                        });

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Snackbar.make(recyclerview_guokr,"加载失败！",Snackbar.LENGTH_SHORT).show();
                refresh_guokr.post(new Runnable() {
                    @Override
                    public void run() {
                        refresh_guokr.setRefreshing(false);
                    }
                });

            }
        });

        request.setTag(TAG);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (refresh_guokr.isRefreshing()){
            refresh_guokr.setRefreshing(false);
        }
    }

}
