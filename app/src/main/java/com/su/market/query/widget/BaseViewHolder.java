package com.su.market.query.widget;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

public class BaseViewHolder extends RecyclerView.ViewHolder {
    private Map<Integer, View> mViewMap;

    public BaseViewHolder(View itemView) {
        super(itemView);
        mViewMap = new HashMap<>();
    }

    /**
     * 根据id获取布局上的view
     */
    @SuppressWarnings("unchecked")
    public <S extends View> S getView(int id) {
        View view = mViewMap.get(id);
        if (view == null) {
            view = itemView.findViewById(id);
            mViewMap.put(id, view);
        }
        return (S) view;
    }
}
