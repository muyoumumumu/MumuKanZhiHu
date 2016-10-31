package com.muyoumumumu.mumukanzhihu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.muyoumumumu.mumukanzhihu.R;
import com.muyoumumumu.mumukanzhihu.bean.GuokrPost;
import com.muyoumumumu.mumukanzhihu.other.OnRecyclerViewOnClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amumu on 2016/10/22.
 */

public class GuokrPostAdapter extends RecyclerView.Adapter<GuokrPostAdapter.ViewHolder> {

    private List<GuokrPost> list=new ArrayList<>();
    private Context context;
    private OnRecyclerViewOnClickListener mListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_guokr,parent,false),mListener);
    }

    public GuokrPostAdapter(Context context, List<GuokrPost> list) {
        super();
        this.context=context;
        this.list=list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setItemClickListener(OnRecyclerViewOnClickListener listener){
        this.mListener = listener;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        GuokrPost post=list.get(position);

        holder.title.setText(post.getTitle());
        holder.summary.setText(post.getSummary());

        Glide.with(context)
                .load(post.getImg())
                .asBitmap()
                .centerCrop()
                .into(holder.img);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener {
        private TextView title;
        private ImageView img;
        private TextView summary;

        OnRecyclerViewOnClickListener listener;

        public ViewHolder(View itemView, OnRecyclerViewOnClickListener listener) {
            super(itemView);

            title=(TextView)itemView.findViewById(R.id.guokr_title);
            img=(ImageView) itemView.findViewById(R.id.guokr_img);
            summary=(TextView)itemView.findViewById(R.id.guokr_summary);

            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null){
                listener.OnItemClick(v,getLayoutPosition());
            }
        }
    }



}
