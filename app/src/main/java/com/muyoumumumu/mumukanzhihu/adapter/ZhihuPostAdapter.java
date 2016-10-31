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
import com.muyoumumumu.mumukanzhihu.bean.ZhihuPost;
import com.muyoumumumu.mumukanzhihu.other.OnRecyclerViewOnClickListener;

import java.util.ArrayList;
import java.util.List;


/**
 * zhihu Adapter
 * Created by amumu on 2016/8/27.
 */

//RecyclerView.Adapter<ZhihuPostAdapter.LatestItemViewHolder>中还必须写
public class ZhihuPostAdapter extends RecyclerView.Adapter<ZhihuPostAdapter.LatestItemViewHolder> {

    private final Context context;
    private List<ZhihuPost> list = new ArrayList<>();
    //  LayoutInflater
    private final LayoutInflater layoutInflater;
    //Listener
    private OnRecyclerViewOnClickListener listener;

    public ZhihuPostAdapter(Context context, List<ZhihuPost> list) {
        this.context = context;
        this.list = list;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public LatestItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.zhihu_item_layout, parent, false);

        return new LatestItemViewHolder(v,listener);
    }

    @Override
    public void onBindViewHolder(LatestItemViewHolder holder, int position) {
        ZhihuPost item =list.get(position);
        if(item.getFirstImg()==null){
            holder.itemImg.setImageResource(R.drawable.background);
        }else{
            //图片加载库
            /*
            *
            * with(Context context). 使用Application上下文，Glide请求将不受Activity/Fragment生命周期控制。
            * with(Activity activity).使用Activity作为上下文，Glide的请求会受到Activity生命周期控制。
            * with(FragmentActivity activity).Glide的请求会受到FragmentActivity生命周期控制。
            * with(android.app.Fragment fragment).Glide的请求会受到Fragment 生命周期控制。
            * with(android.support.v4.app.Fragment fragment).Glide的请求会受到Fragment生命周期控制。
            */
            Glide.with(context)
                    //Glide基本可以load任何可以拿到的媒体资源
                    .load(item.getFirstImg())
                    .error(R.drawable.background)
                    //?
                    .centerCrop()
                    .into(holder.itemImg);
        }
        holder.itemTitle.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setItemClickListener (OnRecyclerViewOnClickListener listener){
        this.listener=listener;
    }

    public class LatestItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView itemImg;
        private TextView itemTitle;
        private OnRecyclerViewOnClickListener listener;

        public LatestItemViewHolder(View view, OnRecyclerViewOnClickListener listener) {
            super(view);

            itemImg = (ImageView) view.findViewById(R.id.zhihu_item_img);
            itemTitle = (TextView) view.findViewById(R.id.zhihu_item_title);
            this.listener = listener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null){
                listener.OnItemClick(view,getLayoutPosition());
            }
        }
    }

}





