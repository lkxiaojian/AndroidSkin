package com.zky.myskin.activitys;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zky.skinlibrary.SkinManager;

/**
 * Created by lk
 * Date 2020/7/8
 * Time 17:55
 * Detail:
 */
public class BaseHolder extends RecyclerView.ViewHolder{

    public BaseHolder(@NonNull View itemView) {
        super(itemView);
        SkinManager.Companion.getInstance().applyViews(itemView);
    }
}
