package com.muyoumumumu.mumukanzhihu.fragment;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.muyoumumumu.mumukanzhihu.activity.ZhihuReadActivity;
import com.muyoumumumu.mumukanzhihu.adapter.ZhihuPostAdapter;
import com.muyoumumumu.mumukanzhihu.bean.ZhihuPost;
import com.muyoumumumu.mumukanzhihu.other.Api;
import com.muyoumumumu.mumukanzhihu.other.DividerItemDecoration;
import com.muyoumumumu.mumukanzhihu.other.MyDbHelper;
import com.muyoumumumu.mumukanzhihu.other.NetworkState;
import com.muyoumumumu.mumukanzhihu.other.OnRecyclerViewOnClickListener;
import com.rey.material.app.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ZhihuFragment extends Fragment {

    private SwipeRefreshLayout refrash;
    private RecyclerView zhihuMain;
    private FloatingActionButton zhihuFab;
    private List<ZhihuPost> list = new ArrayList<>();
    private ZhihuPostAdapter adapter;

    // 2013.5.20是知乎日报api首次上线
    private int year;
    private int month;
    private int day;

    // 用于记录加载更多的次数
    private int groupCount = -1;
    private final String TAG = "ZhihuFragment";
    //  请求队列
    private RequestQueue queue;
    private SQLiteDatabase db;

    //设置一些 后台的东西
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //既然是Http操作，自然有请求和响应，RequestQueue是一个请求队列对象，
        // 它可以缓存所有的HTTP请求，然后按照一定的算法并发地发出这些请求
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        MyDbHelper dbHelper = new MyDbHelper(getActivity(), "HistoryPost.db", null, 1);
        db = dbHelper.getWritableDatabase();

    }

    public static ZhihuFragment newInstance() {

        Bundle args = new Bundle();

        ZhihuFragment fragment = new ZhihuFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_zhihu, container, false);

        //设置刷新控件
        refrash = (SwipeRefreshLayout) view.findViewById(R.id.refrash);
        //故意写错
        refrash.setColorSchemeResources(R.color.colorAccent);
        //刷新距离
        refrash.setDistanceToTriggerSync(150);
        //设置背景颜色
        refrash.setProgressBackgroundColorSchemeColor(Color.WHITE);
        //设置大小
        refrash.setSize(SwipeRefreshLayout.DEFAULT);

        zhihuMain = (RecyclerView) view.findViewById(R.id.zhihu_main);
        zhihuMain.setLayoutManager(new LinearLayoutManager(getContext()));
        //绘制分割线
        zhihuMain.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        zhihuFab = (FloatingActionButton) view.findViewById(R.id.zhihu_fab);

        //刷新当前date
        initDate();

        //由于 我开始返回的的三个页面都是同一个，
        // 而SnackBar只有一个实例对象
        // 所以它出现在了最后的一个页面，你没看见
        if (!NetworkState.networkConnected(getActivity())) {
            showNoNetwork();
            loadFromDB();
        } else {
            load_zhihu(null);
        }

        refrash.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //刷新当前date
                initDate();

                //删除原有的内容
                if (!list.isEmpty()) {
                    list.clear();

                }

                if (!NetworkState.networkConnected(getActivity())) {
                    showNoNetwork();
                    //loadFromDB====================================================================
                    loadFromDB();
                } else {
                    //载入最新
                    load_zhihu(null);
                }

                initDate();

            }
        });

        zhihuFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final DatePickerDialog dialog = new DatePickerDialog(getActivity());

                //设置picker的默认时间，不设置则为空
                dialog.date(day, month, year);

                //设置可选日期的 开始时间
                Calendar calendar = Calendar.getInstance();
                calendar.set(2000, 1, 1);
                //设置时间跨度
                dialog.dateRange(calendar.getTimeInMillis(), Calendar.getInstance().getTimeInMillis());

                dialog.show();

                //设置确认，取消
                dialog.positiveAction(getString(R.string.yes));

                dialog.positiveActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //改善下次选择日期的 待选日期
                        year = dialog.getYear();
                        month = dialog.getMonth();
                        day = dialog.getDay();

                        //根据选定日期载入知乎信息数据

                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

                        String date = dialog.getFormattedDate(format);

                        //逼得我用这个
                        System.out.println("输出的东西" + date);

                        load_zhihu(date);

                        dialog.dismiss();

                    }
                });

                dialog.negativeAction(getString(R.string.no));

                dialog.negativeActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

            }
        });


        //setOnScrollListener已不建议使用
        zhihuMain.addOnScrollListener(new RecyclerView.OnScrollListener() {

            boolean isSlidingToLast = false;

            //是否是向下滑动
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isSlidingToLast = dy > 0;
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //不滚动
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 获取最后一个__**完全**__显示的itemPosition
                    int lastVisableItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = linearLayoutManager.getItemCount();

                    //如果向下滑动并且是到最后一个
                    if (lastVisableItem + 1 == totalItemCount && isSlidingToLast) {
                        loadMore();
                    }
                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        //这句一定要最后执行
        return view;
    }

    //加载更多
    private void loadMore() {
        //设定前一天
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day - groupCount);
        Date d = calendar.getTime();
        final String date = format.format(d);

        JsonObjectRequest request = new JsonObjectRequest(Api.HISTORY + date, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                try {

                    if (!jsonObject.getString("date").isEmpty()) {

                        JSONArray jsonArray = jsonObject.getJSONArray("stories");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            //一张照片还是方括号
                            JSONArray imges = jsonArray.getJSONObject(i).getJSONArray("images");
                            String id = jsonArray.getJSONObject(i).getString("id");
                            String type = jsonArray.getJSONObject(i).getString("type");
                            String title = jsonArray.getJSONObject(i).getString("title");
                            List<String> stringList = new ArrayList<>();
                            String imgUrl = imges.getString(0);
                            stringList.add(imgUrl);

                            ZhihuPost item = new ZhihuPost(title, stringList, type, id);

                            list.add(item);

                            if (!query_id_exists("Posts", id)) {
                                ContentValues values = new ContentValues();
                                values.put("_id", Integer.valueOf(id));
                                values.put("title", title);
                                values.put("type", Integer.valueOf(type));
                                values.put("img_url", imgUrl);

                                if (date == null) {
                                    String d = jsonObject.getString("date");
                                    values.put("date", Integer.valueOf(d));
                                    storeContent(id, d);
                                } else {
                                    values.put("date", Integer.valueOf(date));
                                    storeContent(id, date);
                                }

                                //插入
                                db.insert("Posts", null, values);
                                values.clear();
                            }
                        }


                    } else {
                        Snackbar.make(zhihuFab, "加载错误", Snackbar.LENGTH_SHORT).show();
                        if (refrash.isRefreshing()) {
                            refrash.post(new Runnable() {
                                @Override
                                public void run() {
                                    refrash.setRefreshing(false);
                                }
                            });
                        }
                    }

                    /////////////.......
                    adapter.notifyDataSetChanged();
                    //天数加一
                    groupCount++;

                } catch (Exception e) {
                    Snackbar.make(zhihuFab, "加载错误", Snackbar.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                Snackbar.make(zhihuFab, "加载错误", Snackbar.LENGTH_SHORT).show();
                if (refrash.isRefreshing()) {
                    refrash.post(new Runnable() {
                        @Override
                        public void run() {
                            refrash.setRefreshing(false);
                        }
                    });
                }
            }
        });

        request.setTag(TAG);
        queue.add(request);
    }

    private void load_zhihu(final String date) {

        //重点关注
        refrash.post(new Runnable() {
            @Override
            public void run() {
                refrash.setRefreshing(true);
            }
        });

        String url;

        if (!list.isEmpty())
            list.clear();

        if (date == null) {
            //最新消息
            url = Api.LATEST;
        } else {
            url = Api.HISTORY + date;
        }

        //重点,多记==============================================
        //附表
        /*
        *           date            20160904
        * response  stories         同下
        *           top_stories     images          底图（JsonArray形式）
        *                           id              id
        *                           ga_prefix       什么前缀
        *                           type            不明
        *                           title           标题
        *                           multipic        中等图片？？
        * */
        //JsonObjectRequest(in method,url,Listener,ErrorListener)
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                try {
                    //也就是说有数据
                    if (!jsonObject.getString("date").isEmpty()) {

                        JSONArray array = jsonObject.getJSONArray("stories");

                        //获取数据队列
                        for (int i = 0; i < array.length(); i++) {
                            //images是个数组形式
                            JSONArray image = array.getJSONObject(i).getJSONArray("images");
                            String id = array.getJSONObject(i).getString("id");
                            String type = array.getJSONObject(i).getString("type");
                            String title = array.getJSONObject(i).getString("title");

                            //获取图片地址，其实都只有一张图片  ==、
                            List<String> imgStringList = new ArrayList<>();
                            for (int j = 0; j < image.length(); j++) {
                                String imgurl = image.getString(j);
                                imgStringList.add(imgurl);
                            }
                            //..............
                            ZhihuPost item = new ZhihuPost(title, imgStringList, type, id);

                            list.add(item);

                            //如果不存在重复的内容
                            if (!query_id_exists("Posts", id)) {

                                ContentValues values = new ContentValues();
                                values.put("_id", Integer.valueOf(id));
                                values.put("title", title);
                                values.put("type", Integer.valueOf(type));
                                //显示头张图片应该是封面底图
                                values.put("img_url", imgStringList.get(0));

                                //如果时间选择器中没有时间数据，就取响应中的date
                                //这样会快一点？？
                                if (date == null) {
                                    String json_date = jsonObject.getString("date");
                                    values.put("date", Integer.valueOf(json_date));
                                    storeContent(id, json_date);
                                } else {
                                    values.put("date", Integer.valueOf(date));
                                    storeContent(id, date);
                                }

                                db.insert("Posts", null, values);

                                //清缓存好青年
                                values.clear();
                            }
                        }
                    }
                    ///================================================
                    adapter = new ZhihuPostAdapter(getActivity(), list);
                    zhihuMain.setAdapter(adapter);
                    adapter.setItemClickListener(new OnRecyclerViewOnClickListener() {
                        @Override
                        public void OnItemClick(View v, int position) {
                            startActivity(new Intent(getActivity(), ZhihuReadActivity.class)
                                    .putExtra("id", list.get(position).getId())
                                    //老是first_image装不进去
                                    .putExtra("first_image", list.get(position).getFirstImg())
                                    .putExtra("title", list.get(position).getTitle())
                            );


                        }
                    });

                    if (refrash.isRefreshing()) {

                        //可能这个线程在refrash,setRefrashing(ture)之前就已经结束了==。
                        refrash.post(new Runnable() {
                            @Override
                            public void run() {
                                refrash.setRefreshing(false);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

                , new Response.ErrorListener()

        {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (refrash.isRefreshing()) {
                    Snackbar.make(zhihuFab, "加载失败", Snackbar.LENGTH_SHORT);
                    refrash.post(new Runnable() {
                        @Override
                        public void run() {
                            refrash.setRefreshing(false);
                        }
                    });
                }
            }

        });

        request.setTag(TAG);
        queue.add(request);


    }

    //传入id和date日期，后下载数据将数据存在数据库中，、
    //表
    /*
    * response              body    (主体)
    *                       image_source
    *                       title
    *                       image
    *                       share_url
    *                       js
    *                       ga_prefix
    *                       images
    *                       type
    *                       css
    *                       id
    * */
    private void storeContent(final String id, final String json_date) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Api.NEWS + id, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                //存在就加载内容
                if (query_id_exists("Posts", id)) {
                    ContentValues values = new ContentValues();

                    try {
                        if (!jsonObject.isNull("body")) {
                            values.put("_id", Integer.valueOf(id));
                            values.put("content", jsonObject.getString("body"));
                            values.put("date", Integer.valueOf(json_date));
                            db.insert("Contents", null, values);
                            values.clear();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (refrash.isRefreshing()) {
                    Snackbar.make(zhihuFab, "加载失败", Snackbar.LENGTH_SHORT);
                    refrash.post(new Runnable() {
                        @Override
                        public void run() {
                            refrash.setRefreshing(false);
                        }
                    });
                }
            }
        });

        request.setTag(TAG);
        queue.add(request);

    }

    private void initDate() {

        //实例化了一个日历
        Calendar c = Calendar.getInstance();
        //设置日历日期为昨天
        c.add(Calendar.DAY_OF_MONTH, -1);
        //取得昨天的年月日
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

    }

    private void loadFromDB() {
        //设置刷新
        refrash.post(new Runnable() {
            @Override
            public void run() {
                refrash.setRefreshing(true);
            }
        });

        Cursor cursor = db.query("Posts", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                List<String> list = new ArrayList<>();
                list.add(cursor.getString(cursor.getColumnIndex("img_url")));
                String id = String.valueOf(cursor.getColumnIndex("_id"));
                String type = String.valueOf(cursor.getColumnIndex("type"));

                if ((title != null) && (list.get(0) != null) && (!id.equals("") && (!type.equals("")))) {
                    ZhihuPost item = new ZhihuPost(title, list, type, id);
                    this.list.add(item);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new ZhihuPostAdapter(getActivity(), list);
        zhihuMain.setAdapter(adapter);
        adapter.setItemClickListener(new OnRecyclerViewOnClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                startActivity(new Intent(getActivity(), ZhihuReadActivity.class)
                        .putExtra("id", list.get(position).getId())
                        .putExtra("title", list.get(position).getId())
                        .putExtra("image", list.get(position).getFirstImg()));

            }
        });

        //刷新停止
        refrash.post(new Runnable() {
            @Override
            public void run() {
                refrash.setRefreshing(false);
            }
        });
    }

    public void showNoNetwork() {
        Snackbar.make(zhihuFab, R.string.noNetwork, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.go_to_set, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                }).show();

    }

    private boolean query_id_exists(String tableName, String id) {

        Cursor cursor = db.query(tableName, null, null, null, null, null, null);

        //并没用else
        if (cursor.moveToFirst()) {

            do {
                //如果id行的数据与id相等
                if (id.equals(String.valueOf(cursor.getInt(cursor.getColumnIndex("_id")))))
                    return true;
            } while (cursor.moveToNext());

        }
        //关游标好青年
        cursor.close();
        return false;
    }


}
