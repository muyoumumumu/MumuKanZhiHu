package com.muyoumumumu.mumukanzhihu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;import com.muyoumumumu.mumukanzhihu.R;
import com.muyoumumumu.mumukanzhihu.bean.ZhiHuComment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 评论适配器,好难写
 * Created by amumu on 2016/10/22.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {

    private Context context;
    private List<ZhiHuComment> list;
    private LayoutInflater layoutInflater;

    public CommentsAdapter(Context context, List<ZhiHuComment> list) {
        this.context = context;
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommentsViewHolder(layoutInflater.inflate(R.layout.item_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(CommentsViewHolder holder, int position) {

        ZhiHuComment zhiHuComment = list.get(position);
        holder.author.setText(zhiHuComment.getAuthor());
        holder.content.setText(zhiHuComment.getContent());

        Glide.with(context)
                .load(zhiHuComment.getAvatar())
                .into(holder.avatar);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(Long.valueOf(zhiHuComment.getTime())*1000);
        holder.time.setText(simpleDateFormat.format(date));

        holder.likes.setText(zhiHuComment.getLikes() + "");

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder {

        private TextView author;
        private TextView content;
        private ImageView avatar;
        private TextView time;
        private TextView likes;

        public CommentsViewHolder(View view) {
            super(view);

            author = (TextView) view.findViewById(R.id.author);
            content = (TextView) view.findViewById(R.id.content);
            avatar = (ImageView) view.findViewById(R.id.avatar);
            time = (TextView) view.findViewById(R.id.time);
            likes = (TextView) view.findViewById(R.id.likes);
        }

    }

}
