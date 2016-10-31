package com.muyoumumumu.mumukanzhihu.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.muyoumumumu.mumukanzhihu.R;
import com.muyoumumumu.mumukanzhihu.fragment.GuokrFragment;
import com.muyoumumumu.mumukanzhihu.fragment.ZhihuFragment;


/**
 * Created by amumu on 2016/8/21.
 * 不把adapter写完就会出错
 */
public class MyAdapter extends FragmentPagerAdapter {

    private String[] titles;

    public MyAdapter(FragmentManager fm, Context context) {
        super(fm);

        //很重要的一句
        titles = context.getResources().getStringArray(R.array.titles);
    }


    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return ZhihuFragment.newInstance();
        } if(position==1){
            return GuokrFragment.newInstance();
        }else {
            return null;
        }
    }

    //Return the number of views available
    @Override
    public int getCount() {
        return titles.length;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
