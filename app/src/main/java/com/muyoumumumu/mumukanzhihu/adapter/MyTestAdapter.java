package com.muyoumumumu.mumukanzhihu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.muyoumumumu.mumukanzhihu.R;


/**
 * \'\'\
 * Created by amumu on 2016/9/18.
 */
public class MyTestAdapter extends RecyclerView.Adapter<MyTestAdapter.MyViewHolder>{

    String[] datas =null;

    public  MyTestAdapter(String[] datas) {
        this.datas = datas;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
       holder.textView.setText(datas[position]);
    }

    @Override
    public int getItemCount() {
        return datas.length;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView  ;
        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
        }
    }
}
