package com.luoyangwei.localclient.view.pictures;

import android.view.View;

import com.luoyangwei.localclient.data.model.Resource;

public interface PicturesItemOnClickListener {

    /**
     * 点击事件
     *
     * @param v        view
     * @param position 位置
     * @param resource 资源
     */
    void onClick(View v, int position, Resource resource);

}
